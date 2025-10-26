package com.magic.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.magic.cache.redis.RedisCacheUtils;
import com.magic.constant.Constants;
import com.magic.emum.BizErrorEnum;
import com.magic.mybatisplus.entity.TConfig;
import com.magic.mybatisplus.entity.TSymbolConfig;
import com.magic.mybatisplus.mapper.TConfigMapper;
import com.magic.mybatisplus.mapper.TOrderMapper;
import com.magic.mybatisplus.mapper.TSymbolConfigMapper;
import com.magic.mybatisplus.service.TOrderService;
import com.magic.service.LarkService;
import com.magic.service.QuantTask;
import com.magic.vo.resp.base.HealthCheckResp;
import com.magic.vo.resp.base.ResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/service")
public class ServiceController {
    @Value("${quant.configId}")
    private Integer configIdFromConfigFile;

    @Autowired
    TConfigMapper       configMapper;
    @Autowired
    TSymbolConfigMapper symbolConfigMapper;
    @Autowired
    TOrderMapper        orderMapper;
    @Autowired
    TOrderService       orderService;
    @Autowired
    RedisCacheUtils     redisClient;
    @Autowired
    LarkService         larkService;

    long sequenceId = 0L;

    // configId, QuantTask
    public static ConcurrentHashMap<Integer, QuantTask> hashmapQuantTask = new ConcurrentHashMap<>();

    @GetMapping("/start")
    public synchronized ResponseBase start(Integer configId) {
        log.info("start configId:{}, configIdLocal:{}", configId, configIdFromConfigFile);

        if (configId == null || configId <= 0) {
            log.info("start failed, bad  config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_INVALID);
        }

        if (configId != configIdFromConfigFile.intValue()) {
            log.info("start failed, mismatch config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_MISMATCH);
        }

        // get config
        TConfig config = configMapper.selectById(configId);
        if (config == null) {
            log.info("start failed, config isn't exist, id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_NOT_EXIST);
        }

        // get symbol config
        TSymbolConfig symbolConfig = Constants.getSymbolConfig(config.getSymbol());

        if (symbolConfig == null) {
            symbolConfig = new LambdaQueryChainWrapper<>(symbolConfigMapper)
                    .eq(TSymbolConfig::getSymbol, config.getSymbol().toUpperCase())
                    .one();
            log.info("start get symbol from db, symbol:{}", symbolConfig.getSymbol());
        }

        if (symbolConfig == null) {
            log.info("start failed, symbol config isn't exist, id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_SYMBOL_CONFIG_NOT_EXIST);
        }

//        log.info("start config:{}", new JSONObject(config).toStringPretty());

//        {
//            JSONObject joSymbol = new JSONObject(symbolConfig);
//
//             价格精度检查
//            int scale = symbolConfig.getTickSize();
//            if (scale < 0 || scale > 4) {
//                log.info("start failed, bad scale, param:{}", joSymbol.toStringPretty());
//                return ResponseBase.fail(BizErrorEnum.ERROR_PRICE_SCALE_INVALID);
//            }
//        }

        log.info("start configNew:{}", new JSONObject(config).toStringPretty());

        QuantTask quantTask = null;
        boolean   update    = false;

        synchronized (ServiceController.class) {
            quantTask = hashmapQuantTask.get(configId);
            if (quantTask != null) {
                log.info("start done, already started, configId:{}", configId);
                return ResponseBase.fail(BizErrorEnum.ERROR_STARTED_ALREADY);
            }

            // do create
            quantTask = new QuantTask(config, symbolConfig, orderService, redisClient, configMapper, larkService);

            // do start
            ResponseBase resp = quantTask.start(config, symbolConfig);
            if (!resp.isSuccess()) {
                log.info("start failed, configId:{}", configId);
                return resp;
            }

            update = new LambdaUpdateChainWrapper<>(configMapper)
                    .eq(TConfig::getId, config.getId())
                    .set(TConfig::getStatusService, Constants.QUANT_STATUS_RUNNING)
                    .set(TConfig::getStatusQuant, Constants.QUANT_STATUS_RUNNING)
                    .update();

            hashmapQuantTask.put(configId, quantTask);
        }

        log.info("start done, configId:{}, updateStatus ret:{}", configId, update);
        return ResponseBase.GetResponseSuccess();
    }

    // orderId == null: 取消所有
    // orderId != null: 取消指定
    @GetMapping("/stop")
    public synchronized ResponseBase stop(Integer configId) {
        if (configId == null || configId <= 0) {
            log.info("stop failed, bad config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_INVALID);
        }

        if (configId != configIdFromConfigFile.intValue()) {
            log.info("stop failed, mismatch config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_MISMATCH);
        }

        boolean update = false;
        synchronized (ServiceController.class) {
            QuantTask quantTask = hashmapQuantTask.get(configId);
            if (quantTask == null) {
                log.info("stop failed, service isn't started, configId:{}", configId);
                return ResponseBase.fail(BizErrorEnum.ERROR_STOP_FAILED_NOT_STARTED);
            }

            quantTask.stop();
            hashmapQuantTask.remove(configId);

            update = new LambdaUpdateChainWrapper<>(configMapper)
                    .eq(TConfig::getId, configId)
                    .set(TConfig::getStatusQuant, Constants.QUANT_STATUS_STOPPED)
                    .update();
        }

        log.info("stop done, configId:{}, updateStatus ret:{}", configId, update);
        return ResponseBase.GetResponseSuccess();
    }

    @GetMapping("/configGet")
    public synchronized ResponseBase getConfig(int configId) {
        QuantTask quantTask = null;

        if (configId != configIdFromConfigFile) {
            log.info("configGet failed, mismatch config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_MISMATCH);
        }

        synchronized (ServiceController.class) {
            quantTask = hashmapQuantTask.get(configId);

            if (quantTask == null) {
                log.info("getConfig failed, quant isn't running, configId:{}", configId);
                return ResponseBase.GetResponseSuccess(null);
            }

            TConfig config = quantTask.configGet();
            return ResponseBase.GetResponseSuccess(config);
        }
    }

    @GetMapping("/symbolConfigGet")
    public synchronized ResponseBase getSymbolConfig(int configId) {
        QuantTask quantTask = null;

        if (configId != configIdFromConfigFile) {
            log.info("symbolConfigGet failed, mismatch config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_MISMATCH);
        }

        synchronized (ServiceController.class) {
            quantTask = hashmapQuantTask.get(configId);

            if (quantTask == null) {
                log.info("getSymbolConfig failed, quant isn't running, configId:{}", configId);
                return ResponseBase.fail(BizErrorEnum.ERROR_NOT_STARTED);
            }

            TSymbolConfig config = quantTask.symbolConfigGet();
            return ResponseBase.GetResponseSuccess(config);
        }
    }

    @GetMapping("/symbolConfigReload")
    public synchronized ResponseBase reloadSymbolConfig(int configId) {

        if (configId != configIdFromConfigFile) {
            log.info("symbolConfigReload failed, mismatch config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_MISMATCH);
        }

        // get config
        TConfig config = configMapper.selectById(configId);
        if (config == null) {
            log.info("symbolConfigReload failed, config isn't exist, id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_NOT_EXIST);
        }
        log.info("symbolConfigReload config:{}", new JSONObject(config).toStringPretty());

        // get symbol config
        TSymbolConfig symbolConfig = new LambdaQueryChainWrapper<>(symbolConfigMapper)
                .eq(TSymbolConfig::getSymbol, config.getSymbol().toUpperCase())
                .one();

        if (symbolConfig == null) {
            log.info("symbolConfigReload failed, symbol config isn't exist, id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_SYMBOL_CONFIG_NOT_EXIST);
        }
        log.info("symbolConfigReload config:{}", new JSONObject(symbolConfig).toStringPretty());

        QuantTask quantTask = null;
        synchronized (ServiceController.class) {
            quantTask = hashmapQuantTask.get(configId);

            if (quantTask == null) {
                log.info("reloadSymbolConfig failed, quant isn't running, configId:{}", configId);
                return ResponseBase.fail(BizErrorEnum.ERROR_NOT_STARTED);
            }

            TSymbolConfig symbolConfigNew = quantTask.symbolConfigSet(symbolConfig);
            return ResponseBase.GetResponseSuccess(symbolConfigNew);
        }
    }

    @GetMapping("/healthCheck")
    public synchronized ResponseBase healthCheck(int configId) {
        if (configId != configIdFromConfigFile) {
            log.info("healthCheck failed, mismatch config id:{}", configId);
            return ResponseBase.fail(BizErrorEnum.ERROR_CONFIG_ID_MISMATCH);
        }

        HealthCheckResp resp = new HealthCheckResp();
        resp.setSequenceId(sequenceId++ % 1000000L);
        resp.setConfigId(configId);
        resp.setTimestamp(System.currentTimeMillis());

        return ResponseBase.GetResponseSuccess(resp);
    }

}
