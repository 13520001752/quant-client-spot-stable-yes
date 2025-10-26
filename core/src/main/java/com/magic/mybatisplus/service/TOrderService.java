package com.magic.mybatisplus.service;

import com.magic.mybatisplus.entity.TOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author magic beans
 * @since 2023-11-29
 */
public interface TOrderService extends IService<TOrder> {
//    public TOrder parse (long apiKeyId, String apiKey, int exchangeId, JSONObject jo);

    //public TOrder saveOrUpdate(long apiKeyId, String apiKey, int exchangeId, JSONObject jo);

    public List<TOrder> getByStatus(String type, String status);

    public List<TOrder> getNotFinished(int exchangeId, String apiKey, String symbol);

    public TOrder getLastFilledOrder(String apiKey, int exchangeId, String Symbol);


    public int doUpdate(TOrder orderNew);

    public int doUpdate(List<TOrder> orderList);

}
