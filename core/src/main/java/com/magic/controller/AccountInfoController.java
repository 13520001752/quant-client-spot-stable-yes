package com.magic.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.exceptions.BinanceClientException;
import com.magic.emum.BizErrorEnum;
import com.magic.mybatisplus.entity.TConfig;
import com.magic.service.*;
import com.magic.vo.resp.base.ResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/")
public class AccountInfoController {
    @Autowired
    CommonService     commonService;

    @GetMapping("/getAccountInfo")
    public ResponseBase getAccountInfo(@RequestParam(required = true, name = "configId") Integer configId) {

        TConfig    config            = commonService.getConfig(configId);
        SpotClient clientAccountHTTP = commonService.getClientAccountHTTP(configId);
        String     symbol            = config.getSymbol();

        String     str1 = null;
        JSONObject jo   = null;

        try {
            str1 = clientAccountHTTP.createTrade().account(new LinkedHashMap<>());
            jo = new JSONObject(str1);

            log.info("3000 getAccountInfo resp:{}", jo.toStringPretty());
        } catch (BinanceClientException e) {
            log.info("3000 getAccountInfo failed to get accountInformation, error:", e);
            return ResponseBase.fail(e.getErrorCode(), e.getErrMsg(), null);
        } catch (Exception e) {
            log.info("3000 getAccountInfo failed to get accountInformation, error:", e);
            return ResponseBase.fail(BizErrorEnum.ERROR_GET_ACCOUNT_INFO, e.getMessage());
        }

        return ResponseBase.success(jo);
    }

    @GetMapping("/getPositionRisk")
    public ResponseBase getPositionRisk(@RequestParam(required = true, name = "configId") Integer configId) {
        JSONArray ja = new JSONArray();

        return ResponseBase.success(ja);
    }

    @GetMapping("/getPositionRiskAll")
    public ResponseBase getPositionRiskAll(@RequestParam(required = true, name = "configId") Integer configId) {
        JSONArray ja = new JSONArray();

        return ResponseBase.success(ja);
    }

}
