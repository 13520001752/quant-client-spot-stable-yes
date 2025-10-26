package com.magic.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magic.cache.redis.RedisCacheUtils;
import com.magic.mybatisplus.entity.TConfig;
import io.undertow.server.handlers.proxy.mod_cluster.Balancer;
import jdk.jfr.DataAmount;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
@Service
public class LarkService {
    @Autowired
    RedisCacheUtils redisClient;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ConcurrentLinkedQueue<JSONObject> fifo = new ConcurrentLinkedQueue<>();

    public static final String UserDataSubOnOpen    = "订阅OnOpen成功";
    public static final String UserDataSubOnFailure = "订阅OnFailure";
    public static final String UserDataSubOnClose   = "订阅OnClose";
    public static final String UserDataSubExit      = "订阅TaskExit";
    public static final String UserDataSubFailed    = "订阅失败";

    public static final String UserDataListenKeyCreateSuccess = "创建ListenKey成功";
    public static final String UserDataListenKeyCreateFailed  = "创建ListenKey失败";
    public static final String UserDataListenKeyCreateForce   = "强制重新订阅";
    public static final String UserDataListenKeyExtendSuccess = "延期ListenKey成功";
    public static final String UserDataListenKeyExtendFailed  = "延期ListenKey失败";

    public static final String StopOrderPlaced                    = "止损单下单:完成";
    public static final String StopOrderPlaceFailed               = "止损单下单:失败";
    public static final String StopOrderCancelledLiqPriceIsZero   = "止损单取消:爆仓价为0";
    public static final String StopOrderCancelledLiqPriceNoChange = "止损单取消:爆仓价未变化";

    // 业务告警机器人
    private static final String RobotBusinessURL    = "https://open.larksuite.com/open-apis/bot/v2/hook/829b0215-5b44-4b64-a999-d490836bbb7e";
    private static final String RobotBusinessSecret = "538LMxN0rI7k6kxxLwYUTb";

    // 系统告警机器人
    private static final String RobotOPSURL    = "https://open.larksuite.com/open-apis/bot/v2/hook/af36a776-619a-4541-afc3-143fd1c3fd8c";
    private static final String RobotOPSSecret = "lHVg9u97HkfPfofVjvrDhf";

    public void sendLarkBalanceInsufficient(
            TConfig config,
            String symbol,
            BigDecimal priceAvg,
            BigDecimal quantity,
            long timestampFilled,
            //BigDecimal priceLiquid,
            String asset1,
            BigDecimal balance1,
            String side
    ) {
        try {
            MessagePart accountNew  = new MessagePart("成交账号", config.getAccountName());
            MessagePart symbolNew   = new MessagePart("成交标的", symbol);
            MessagePart priceNew    = new MessagePart("成交价格", priceAvg.stripTrailingZeros().toPlainString());
            MessagePart quantityNew = new MessagePart("成交数量", quantity.stripTrailingZeros().toPlainString());
            //MessagePart priceLiquidNew = new MessagePart("爆仓价格", priceLiquid.stripTrailingZeros().toPlainString());
            MessagePart asset   = new MessagePart("当前资产", asset1);
            MessagePart balance = new MessagePart("当前余额", balance1.stripTrailingZeros().toPlainString());
            MessagePart timeNow = new MessagePart("事件时间", sdf.format(timestampFilled));

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(symbolNew);
                add(priceNew);
                add(quantityNew);
                add(timeNow);
                //add(priceLiquidNew);
                add(asset);
                add(balance);
            }};

            String title = "订单成交:机器单";
//            if (manualOrder) {
//                title = "订单成交:手动单";
//            }

            title = title + ":" + side;

            addMessageTask(title, list1, config.getLarkName1(), null);
        } catch (Exception e) {
            log.info("SendLarKdReachMax exception:", e);
        }
    }

    public void SendLarKOrderFilled(
            TConfig config,
            String symbol,
            BigDecimal priceAvg,
            BigDecimal quantity,
            long timestampFilled,
            //BigDecimal priceLiquid,
            BigDecimal position,
            Boolean manualOrder,
            String side
    ) {
        try {
            MessagePart accountNew  = new MessagePart("成交账号", config.getAccountName());
            MessagePart symbolNew   = new MessagePart("成交标的", symbol);
            MessagePart priceNew    = new MessagePart("成交价格", priceAvg.stripTrailingZeros().toPlainString());
            MessagePart quantityNew = new MessagePart("成交数量", quantity.stripTrailingZeros().toPlainString());
            //MessagePart priceLiquidNew = new MessagePart("爆仓价格", priceLiquid.stripTrailingZeros().toPlainString());
            MessagePart positionNew    = new MessagePart("持仓数量", position.stripTrailingZeros().toPlainString());
            MessagePart timeNow        = new MessagePart("事件时间", sdf.format(timestampFilled));
            MessagePart gap            = new MessagePart("-------", "-------");
            MessagePart positionMaxNew = new MessagePart("最大持仓", config.getPositionMax().stripTrailingZeros().toPlainString());
            MessagePart positionMaxMin = new MessagePart("最小持仓", config.getPositionMin().stripTrailingZeros().toPlainString());
            MessagePart leverageNew    = new MessagePart("杠杆倍数", config.getLeverage() + "");
            MessagePart serverName     = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(symbolNew);
                add(priceNew);
                add(quantityNew);
                add(timeNow);
                //add(priceLiquidNew);
                add(positionNew);
                add(gap);
                add(positionMaxNew);
                add(positionMaxMin);
                add(leverageNew);
                add(serverName);
            }};

            String title = "订单成交:机器单";
            if (manualOrder) {
                title = "订单成交:手动单";
            }

            title = title + ":" + side;

            addMessageTask(title, list1, config.getLarkName1(), null);
        } catch (Exception e) {
            log.info("SendLarKdReachMax exception:", e);
        }
    }

    public void SendLarkReachMax(
            TConfig config,
            String symbol,
            BigDecimal priceAvg,
            BigDecimal quantity,
            long timestampFilled,
            BigDecimal position
    ) {

        try {
            MessagePart accountNew     = new MessagePart("成交账号", config.getAccountName());
            MessagePart symbolNew      = new MessagePart("成交标的", symbol);
            MessagePart priceNew       = new MessagePart("成交价格", priceAvg.stripTrailingZeros().toPlainString());
            MessagePart quantityNew    = new MessagePart("成交数量", quantity.stripTrailingZeros().toPlainString());
            MessagePart timeNow        = new MessagePart("事件时间", sdf.format(timestampFilled));
            MessagePart gap            = new MessagePart("-------", "-------");
            MessagePart positionNew    = new MessagePart("持仓数量", position.stripTrailingZeros().toPlainString());
            MessagePart positionMaxNew = new MessagePart("最大持仓", config.getPositionMax().stripTrailingZeros().toPlainString());
            MessagePart positionMaxMin = new MessagePart("最小持仓", config.getPositionMin().stripTrailingZeros().toPlainString());
            MessagePart leverageNew    = new MessagePart("杠杆倍数", config.getLeverage() + "");
            MessagePart serverName     = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(symbolNew);
                add(priceNew);
                add(quantityNew);
                add(timeNow);
                add(positionNew);
                add(gap);
                add(positionMaxNew);
                add(positionMaxMin);
                add(leverageNew);
                add(serverName);
            }};

            String title = "取消下单:达到最大持仓";
            addMessageTask(title, list1, config.getLarkName1(), null);
        } catch (Exception e) {
            log.info("SendLarKdReachMax exception:", e);
        }
    }

    public void SendLarkReachMin(
            TConfig config,
            String symbol,
            BigDecimal priceAvg,
            BigDecimal quantity,
            long timestampFilled,
            BigDecimal position
    ) {

        try {
            MessagePart accountNew     = new MessagePart("成交账号", config.getAccountName());
            MessagePart symbolNew      = new MessagePart("成交标的", symbol);
            MessagePart priceNew       = new MessagePart("成交价格", priceAvg.stripTrailingZeros().toPlainString());
            MessagePart quantityNew    = new MessagePart("成交数量", quantity.stripTrailingZeros().toPlainString());
            MessagePart timeNow        = new MessagePart("事件时间", sdf.format(timestampFilled));
            MessagePart gap            = new MessagePart("-------", "-------");
            MessagePart positionNew    = new MessagePart("持仓数量", position.stripTrailingZeros().toPlainString());
            MessagePart positionMaxNew = new MessagePart("最大持仓", config.getPositionMax().stripTrailingZeros().toPlainString());
            MessagePart positionMaxMin = new MessagePart("最小持仓", config.getPositionMin().stripTrailingZeros().toPlainString());
            MessagePart leverageNew    = new MessagePart("杠杆倍数", config.getLeverage() + "");
            MessagePart serverName     = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(symbolNew);
                add(priceNew);
                add(quantityNew);
                add(timeNow);
                add(positionNew);
                add(gap);
                add(positionMaxNew);
                add(positionMaxMin);
                add(leverageNew);
                add(serverName);
            }};

            String title = "取消下单:达到最小持仓";

            addMessageTask(title, list1, config.getLarkName1(), null);
        } catch (Exception e) {
            log.info("SendLarKdReachMin exception:", e);
        }
    }

    public void SendLarkDuplicateOrder(
            TConfig config,
            String symbol,
            String orderId,
            String orderIdLink,
            BigDecimal priceAvg,
            BigDecimal quantity,
            long ts
    ) {

        try {
            MessagePart accountNew     = new MessagePart("异常账号", config.getAccountName());
            MessagePart symbolNew      = new MessagePart("异常标的", symbol);
            MessagePart orderIdNew     = new MessagePart("订单号码", orderId);
            MessagePart orderIdLinkNew = new MessagePart("订单号码", orderIdLink);
            MessagePart priceNew       = new MessagePart("成交价格", priceAvg.stripTrailingZeros().toPlainString());
            MessagePart quantityNew    = new MessagePart("成交数量", quantity.stripTrailingZeros().toPlainString());
            MessagePart timeNow        = new MessagePart("事件时间", sdf.format(ts));
            MessagePart serverName     = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(symbolNew);
                add(orderIdNew);
                add(orderIdLinkNew);
                add(priceNew);
                add(quantityNew);
                add(timeNow);
                add(serverName);
            }};

            String title = "订阅异常:重复订单";

            addMessageTask(title, list1, null, config.getLarkName2());
        } catch (Exception e) {
            log.info("SendLarkDuplicateOrder exception:", e);
        }
    }

    public void SendSubscriptionEvent(TConfig config, String type) {
        try {
            MessagePart timeNow    = new MessagePart("事件时间", sdf.format(System.currentTimeMillis()));
            MessagePart accountNew = new MessagePart("影响账号", config.getAccountName());
            MessagePart serverName = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(timeNow);
                add(serverName);
            }};

            switch (type) {
                case UserDataSubFailed:
                case UserDataSubOnFailure:
                case UserDataListenKeyCreateFailed:
                case UserDataListenKeyExtendFailed:
                    addMessageTask(type, list1, null, config.getLarkName2());
                    break;
                default:
                    addMessageTask(type, list1, config.getLarkName1(), null);
                    break;
            }
        } catch (Exception e) {
            log.info("SendLarKdReachMin exception:", e);
        }
    }

    public void SendStopOrderEvent(TConfig config,
                                   String type,
                                   String symbol,
                                   BigDecimal liqPriceLast,
                                   BigDecimal liqPriceNew
    ) {
        try {
            if (liqPriceLast == null) {
                liqPriceLast = BigDecimal.ZERO;
            }

            if (liqPriceNew == null) {
                liqPriceNew = BigDecimal.ZERO;
            }

            MessagePart timeNow    = new MessagePart("事件时间", sdf.format(System.currentTimeMillis()));
            MessagePart accountNew = new MessagePart("影响账号", config.getAccountName());
            MessagePart symbolNew  = new MessagePart("交易标的", symbol);
            MessagePart liqPrice1  = new MessagePart("爆仓价老", liqPriceLast.stripTrailingZeros().toPlainString());
            MessagePart liqPrice2  = new MessagePart("爆仓价新", liqPriceNew.stripTrailingZeros().toPlainString());
            MessagePart serverName = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(timeNow);
                add(symbolNew);
                add(liqPrice1);
                add(liqPrice2);
                add(serverName);
            }};

            addMessageTask(type, list1, config.getLarkName1(), null);
        } catch (Exception e) {
            log.info("SendLarKdReachMin exception:", e);
        }
    }

    public void SendWebSocketErrorEvent(TConfig config,
                                        String symbol
    ) {
        try {
            MessagePart timeNow    = new MessagePart("事件时间", sdf.format(System.currentTimeMillis()));
            MessagePart accountNew = new MessagePart("影响账号", config.getAccountName());
            MessagePart symbolNew  = new MessagePart("交易标的", symbol);
            MessagePart serverName = new MessagePart("服务器号", config.getServerName() + " ");

            List<MessagePart> list1 = new ArrayList<>() {{
                add(accountNew);
                add(timeNow);
                add(symbolNew);
                add(serverName);
            }};

            String title = "系统告警，订阅异常，请重启+摆单";

            addMessageTask(title, list1, config.getLarkName1(), null);
        } catch (Exception e) {
            log.info("SendLarKdReachMin exception:", e);
        }
    }

    private void addMessageTask(String title, List<MessagePart> list1, String lark1, String lark2) {
        long tsNow = System.currentTimeMillis();

        String redisKeyLarkSequenceId = "robot:lark:sequenceId";

        Long msgId = redisClient.increment(redisKeyLarkSequenceId, 1);
        if (msgId >= 99999) {
            msgId = 0L;

            redisClient.set(redisKeyLarkSequenceId, msgId + "");
        }

        JSONObject jo         = null;
        String     larkUrl    = null;
        String     larkSecret = null;

        if (lark1 != null) {
            try {
                jo = new JSONObject(lark1);
            } catch (Exception e) {
                log.info("addMessageTask failed, bad Lark config:{}", lark1);
                return;
            }

            larkUrl    = jo.getStr("larkUrl");
            larkSecret = jo.getStr("larkSecret");

            if (StringUtils.isBlank(larkUrl) || StringUtils.isBlank(larkSecret)) {
                log.info("addMessageTask failed, bad Lark config:{}", lark1);
                return;
            }

            MessagePart id = new MessagePart("消息编号", msgId + "");
            list1.add(id);

            List<List<MessagePart>> list2 = new ArrayList<>();
            list2.add(list1);

            Post post = new Post();
            post.zh_cn         = new ZhCn();
            post.zh_cn.title   = "[" + msgId + "]:" + title;
            post.zh_cn.content = list2;

            LarkMessage larkMessage = new LarkMessage();
            larkMessage.timestamp    = tsNow / 1000;
            larkMessage.sign         = getLarkSignature(larkSecret, tsNow / 1000);
            larkMessage.msg_type     = "post";
            larkMessage.content      = new Content();
            larkMessage.content.post = post;
            larkMessage.lark         = lark1;

            JSONObject joMsg = new JSONObject(larkMessage);
            fifo.add(joMsg);

        }

        if (lark2 != null) {
            try {
                jo = new JSONObject(lark2);
            } catch (Exception e) {
                log.info("addMessageTask failed, bad Lark config:{}", lark2);
                return;
            }

            larkUrl    = jo.getStr("larkUrl");
            larkSecret = jo.getStr("larkSecret");

            if (StringUtils.isBlank(larkUrl) || StringUtils.isBlank(larkSecret)) {
                log.info("addMessageTask failed, bad Lark config:{}", lark2);
                return;
            }

            MessagePart id = new MessagePart("消息编号", msgId + "");
            list1.add(id);

            List<List<MessagePart>> list2 = new ArrayList<>();
            list2.add(list1);

            Post post = new Post();
            post.zh_cn         = new ZhCn();
            post.zh_cn.title   = "[" + msgId + "]:" + title;
            post.zh_cn.content = list2;

            LarkMessage larkMessage = new LarkMessage();
            larkMessage.timestamp    = tsNow / 1000;
            larkMessage.sign         = getLarkSignature(larkSecret, tsNow / 1000);
            larkMessage.msg_type     = "post";
            larkMessage.content      = new Content();
            larkMessage.content.post = post;
            larkMessage.lark         = lark2;

            JSONObject joMsg = new JSONObject(larkMessage);
            fifo.add(joMsg);

        }
    }

    private String getLarkSignature(String secret, long timestamp) {
        Mac mac = null;
        //把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;

        //使用HmacSHA256算法计算签名
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            log.error("getLarkSignature failed, exception:", e);
        }

        try {
            mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        } catch (InvalidKeyException e) {
            log.error("getLarkSignature failed, exception:", e);
        }

        byte[] signData = mac.doFinal(new byte[]{});
        return new String(Base64.encodeBase64(signData));
    }

    public static void main(String[] args) {
        TConfig config = new TConfig();
        config.setAccountName("1111");
        config.setPositionMax(BigDecimal.valueOf(200));
        config.setPositionMin(BigDecimal.valueOf(100));
        config.setLeverage(20);

        BigDecimal price    = BigDecimal.valueOf(100);
        BigDecimal quantity = BigDecimal.valueOf(200);


        LarkService larkService = new LarkService();

        //larkService.SendLarKOrderFilled(config, "bbb", price, quantity, System.currentTimeMillis(), BigDecimal.ONE, false);
        larkService.SendLarkDuplicateOrder(config, "aa", "orderid1", "orderid2", price, quantity, System.currentTimeMillis());
    }

    @Data
    class LarkMessage {
        public String  msg_type  = "post";
        public Content content   = null;
        public long    timestamp = System.currentTimeMillis() / 1000;
        public String  sign      = null;

        public String lark;
    }

    @Data
    public static class Content {
        public Post post;
    }

    @Data
    public static class Post {
        public ZhCn zh_cn;
    }

    @DataAmount
    public static class ZhCn {
        public String                  title;
        public List<List<MessagePart>> content;
    }

    @Data
    public static class MessagePart {
        public String tag;
        public String text;

        public MessagePart(String key, String value) {
            this.tag  = "text";
            this.text = String.format("%6s : %-20s\n", key, value);
        }

        public String getJSONString() {
            return new JSONObject(this).toString();
        }
    }

    @Scheduled(fixedDelay = 1)
    private void TaskMessage() {
        String     lark    = null;
        JSONObject joLark  = null;
        String     larkUrl = null;

        int numMsg = fifo.size();
        if (numMsg == 0) {
            return;
        }

        int times = 3;
        while (times > 0) {
            --times;

            JSONObject data = fifo.poll();
            if (data == null) {
                break;
            }

            lark = data.getStr("lark");
            if (lark == null) {
                continue;
            }
            data.remove("lark");

            try {
                joLark  = new JSONObject(lark);
                larkUrl = joLark.getStr("larkUrl");
                if (larkUrl == null) {
                    continue;
                }
            } catch (Exception e) {
                log.error("TaskMessage failed, bak msg:{}", data.toStringPretty());
                continue;
            }

            String msg = data.toString();

            msg = msg.replace("USDT", "");
            msg = msg.replace("BTC", "AAPL-B");
            msg = msg.replace("ETH", "AAPL-E");
            msg = msg.replace("DOGE", "AAPL-D");

            String body = HttpRequest.post(larkUrl)
                    .body(msg)
                    .execute()
                    .body();
            QuantUtil.waitMs(300);
        }
    }
}


