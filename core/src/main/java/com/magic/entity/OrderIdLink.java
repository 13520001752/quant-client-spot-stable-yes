package com.magic.entity;

import com.magic.constant.Constants;
import com.magic.mybatisplus.entity.TOrder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service
public class OrderIdLink {
    private String prefix;
    private int    sequenceId;
    private String type;
    private long   timestamp;

    public static OrderIdLink getInstance(String orderIdLink) {
        if (StringUtils.isBlank(orderIdLink)) {
            return null;
        }

        String[] split = orderIdLink.split("-");
        if (split.length != 4) {
            return null;
        }

        // String orderIdBuy  = String.format("%s-%d-%d-%s", configId, ts, seqId, "b");
        // String orderIdSell = String.format("%s-%d-%d-%s", configId, ts, seqId, "s");

//        switch (split[0]) {
//            case Constants.orderPrefix:
//            case Constants.orderPrefixInitBuy:
//                break;
//            default:
//                return null;
//        }

        //                        0   1   2            3
        // 订单号格式: orderPrefix-seq-b/s-milliseconds
        String orderPrefix = split[0];  // configId
        String ts          = split[1];  // timestamp
        String seqString   = split[2];  // seqId
        String bs          = split[3];  // busy/sell

        int  seqId     = -1;
        long timestamp = -1L;
        try {
            seqId     = Integer.parseInt(seqString);
            timestamp = Long.parseLong(ts);
        } catch (NumberFormatException e) {
            log.error("not customized orderIdLink:{}", orderIdLink);
            return null;
        }

        OrderIdLink id = new OrderIdLink();
        id.setPrefix(orderPrefix);
        id.setSequenceId(seqId);
        id.setType(bs);
        id.setTimestamp(timestamp);
        return id;
    }

//    public static boolean isManualOrder (String orderIdLink) {
//        if (StringUtils.isBlank(orderIdLink)) {
//            return true;
//        }
//
//        String[] split = orderIdLink.split("-");
//        if (split.length != 4) {
//            return true;
//        }
//
//        switch (split[0]) {
//            case Constants.orderPrefix:
//            case Constants.orderPrefixInitBuy:
//                break;
//            default:
//                return true;
//        }
//        return false;
//    }
}

