//package com.magic.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.*;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//@Slf4j
//@Component
//public class SendSmsUtil {
//    private final RestTemplate restTemplate;
//
//    public SendSmsUtil(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    // 企信通账号密码
//    private static final String UserName = "bjhdkj";
//    private static final String UserPass = "hdkj2022";
//    private static final String QXTURL   = "http://115.28.112.245:8082/SendMT/SendMessage";
//
//    private static final String UserName_Marketing = "bjhdkjyx";
//    private static final String UserPass_Marketing = "hdkj2023";
//
//    /**
//     * @param mobile  手机号
//     * @param content 内容
//     * @return 0, 成功/1,失败
//     */
//    public int sendQXTSMS(String mobile, String content) throws UnsupportedEncodingException, URISyntaxException {
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        StringBuilder sb = new StringBuilder();
//        sb.append(QXTURL);
//        sb.append("?");
//        sb.append("UserName=" + UserName);
//        sb.append("&UserPass=" + UserPass);
//        sb.append("&Mobile=" + mobile);
//        sb.append("&Content=" + URLEncoder.encode(content, "utf-8"));
//        String      url     = sb.toString();
//        URI         reqUrl  = new URI(url);
//        HttpHeaders headers = new HttpHeaders();
//        MediaType   type    = MediaType.parseMediaType("application/json; charset=UTF-8");
//        headers.setContentType(type);
//        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//
//        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                reqUrl,
//                HttpMethod.POST,
//                httpEntity,
//                String.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            String responseCode = response.getBody().split(",")[0];
//            if ("03".equals(responseCode) || "00".equals(responseCode)) {
//                log.info("短信发送成功!" + mobile + " 发送内容:[" + content + "]");
//                return 0;
//            }
//        }
//        log.info("短信发送失败!" + mobile + " 短信服务方返回内容:[" + response.getBody() + "]");
//        return 1;
//    }
//
//
//    /**
//     * 发送营销短信
//     *
//     * @param mobile  手机号
//     * @param content 内容
//     * @return 0, 成功/1,失败
//     */
//    public int sendQXTMarketingSMS(String mobile, String content) throws UnsupportedEncodingException, URISyntaxException {
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        StringBuilder sb = new StringBuilder();
//        sb.append(QXTURL);
//        sb.append("?");
//        sb.append("UserName=" + UserName_Marketing);
//        sb.append("&UserPass=" + UserPass_Marketing);
//        sb.append("&Mobile=" + mobile);
//        sb.append("&Content=" + URLEncoder.encode(content, "utf-8"));
//        String      url     = sb.toString();
//        URI         reqUrl  = new URI(url);
//        HttpHeaders headers = new HttpHeaders();
//        MediaType   type    = MediaType.parseMediaType("application/json; charset=UTF-8");
//        headers.setContentType(type);
//        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//
//        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                reqUrl,
//                HttpMethod.POST,
//                httpEntity,
//                String.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            String responseCode = response.getBody().split(",")[0];
//            if ("03".equals(responseCode) || "00".equals(responseCode)) {
//                log.info("短信发送成功!" + mobile + " 发送内容:[" + content + "]");
//                return 0;
//            }
//        }
//
//        log.info("短信发送失败!" + mobile + " 短信服务方返回内容:[" + response.getBody() + "]");
//        return 1;
//    }
//}
