package com.magic.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.magic.constant.Constants;
import com.magic.entity.OrderIdLink;
import com.magic.mybatisplus.entity.TOrder;
import com.magic.mybatisplus.mapper.TOrderMapper;
import com.magic.mybatisplus.service.TOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Override
    public List<TOrder> getByStatus(String type, String status) {
        LambdaQueryWrapper<TOrder> query = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(status)) {
            query.eq(TOrder::getOrderStatus, status);
        }

        if (StringUtils.isNotBlank(type)) {
            query.eq(TOrder::getOrderType, type);
        }

        query.eq(TOrder::getDeleted, 0);
        query.orderByDesc(TOrder::getUpdatedAt);
        List<TOrder> list = this.list(query);
        return list;
    }

    @Override
    public List<TOrder> getNotFinished(int exchangeId, String apiKey, String symbol) {
        LambdaQueryWrapper<TOrder> query = new LambdaQueryWrapper<>();

        ArrayList<String> listStatus = new ArrayList<>();
        // listStatus.add(Constants.ORDER_STATUS_TODO); // 通过orderIdLink查询？
        listStatus.add(Constants.ORDER_STATUS_NEW);
        listStatus.add(Constants.ORDER_STATUS_FILLED_PARTIALLY);

        query.eq(TOrder::getApiKey, apiKey);
        query.eq(TOrder::getSymbol, symbol);
        query.eq(TOrder::getExchangeId, exchangeId);
        query.in(TOrder::getOrderStatus, listStatus);

        query.orderByDesc(TOrder::getUpdatedAt);
        List<TOrder> list = this.list(query);
        return list;
    }

    @Override
    public TOrder getLastFilledOrder(String apiKey, int exchangeId, String symbol) {
        LambdaQueryWrapper<TOrder> query = new LambdaQueryWrapper<>();

        query.eq(TOrder::getApiKey, apiKey);
        query.eq(TOrder::getExchangeId, exchangeId);
        query.eq(TOrder::getSymbol, symbol);
        query.eq(TOrder::getOrderStatus, Constants.ORDER_STATUS_FILLED);
        query.gt(TOrder::getOrderPriceAverage, BigDecimal.ZERO);
        //        query.ne(TOrder::getTrackId, -1);                           // -1:手动单, 量化单 > 0
        query.orderByDesc(TOrder::getOrderTimeUpdate);
        query.last("limit 1");

        List<TOrder> list = this.list(query);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //    public static TOrder parse(String apiKey, int exchangeId, JSONObject jo) {
    //
    //        TOrder order = null;
    //        try {
    //            order = parseOrderDataFromHttp(apiKey, exchangeId, jo);
    //        } catch (Exception e) {
    //            log.error("parse exception:{}", e.getMessage());
    //            return null;
    //        }
    //        return order;
    //    }


    //    @Override
    //    public TOrder saveOrUpdate(long apiKeyId, String apiKey, int exchangeId, JSONObject jo) {
    //        TOrder order = parseOrderDataFromHttp(apiKey, exchangeId, jo);
    //
    //        boolean b = this.saveOrUpdate(order);
    //        log.info("save ret:{}", b);
    //        return order;
    //    }


    @Override
    public synchronized int doUpdate(TOrder orderNew) {
        String  orderId       = null;
        TOrder  orderFromDB   = null;
        boolean isManualOrder = false;

        if (orderNew == null) {
            return 0;
        }

        orderId     = orderNew.getOrderId();
        orderFromDB = this.getById(orderId);

        // 解析orderIdLink
//        OrderIdLink orderIdLink = OrderIdLink.getInstance(orderNew.getOrderIdLink());
//        if (orderIdLink == null) {
//            isManualOrder = true;
//        }

        boolean doSave   = false;
        boolean doUpdate = false;

        // DB记录不存在
        if (orderFromDB == null) {
            doSave = true;
        } else {
            // DB记录已经存在
            if (orderNew.getOrderTimeUpdate() >= orderFromDB.getOrderTimeUpdate()) {
                doUpdate = true;
            } else {
                // log.info("order updated done, already, orderId:{}/{}, status:{}, avgPrice:{}", orderNew.getOrderId(), orderNew.getOrderIdLink(), orderNew.getOrderStatus(), orderNew.getOrderPriceAverage());
                return 0;
            }
        }

        boolean result = false;
        if (doSave) {
            result = this.save(orderNew);
        }

        if (doUpdate) {
            result = this.updateById(orderNew);
        }

//        log.info("doUpdate ret:{}, orderId:{}/{}, type:{}/{}, price:{}/{}, stopPrice:{}, status:{}",
//                 result,
//                 orderNew.getOrderId(),
//                 orderNew.getOrderIdLink(),
//                 orderNew.getOrderTypeOriginal(),
//                 orderNew.getOrderType(),
//                 orderNew.getOrderPriceOriginal(),
//                 orderNew.getOrderPriceAverage(),
//                 orderNew.getPriceStop(),
//                 orderNew.getOrderStatus()
//        );
        if (result) {
            return 0;
        }
        return -1;
    }

    public int doUpdate(List<TOrder> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return 0;
        }

        TOrder order = null;
        for (int i = 0; i < orderList.size(); ++i) {
            order = orderList.get(i);

            doUpdate(order);
        }
        return 0;
    }

}
