package com.magic.utils;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sevenmagicbeans
 * @date 2022/12/21
 */
@Slf4j
public class OkhttpUtil {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(256, 6, TimeUnit.SECONDS))
            .writeTimeout(2, TimeUnit.SECONDS)
            .cookieJar(new LocalCookieJar())
            .build();

    static class LocalCookieJar implements CookieJar {
        List<Cookie> cookies;

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            if (null != cookies) {
                return cookies;
            }
            return new ArrayList<Cookie>();
        }

        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            this.cookies = list;
        }
    }

    public static OkHttpClient getOkHttpClient() {
        return  okHttpClient;
    }



    public static String getHttpPostResult(JSONObject bodyParams, String path) {
        //计算接口请求时间
        long firstTime = System.currentTimeMillis();
        //设置请求参数
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(bodyParams));
        //创建请求
        Request request = new Request.Builder()
                .url(path)
                .header("Content-Type", "application/json")
                .header("User-Agent", "PostmanRuntime/7.20.1")
                .header("Accept", "*/*")
                .post(body)
                .build();
//        log.info(">>>>>>getHttpPostResult start to request {}>>>>>>: request:{}, request bodyParams:{}", path, request,bodyParams);
        long firstTime2 = System.currentTimeMillis();
        try {
            Response response = OkhttpUtil.getOkHttpClient().newCall(request).execute();
            String result_body = response.body().string();
            long lastTime = System.currentTimeMillis()-firstTime2;
            if (!response.isSuccessful()) {
                //请求失败，返回空数据
                log.warn(">>>>>>getHttpPostResult {} error code {}. time:{}ms, response: {}",path , response.code() ,lastTime , result_body);
                return null;
            }
            log.info(">>>>>>getHttpPostResult {} success. request cost:{}ms, request bodyParams:{} ,respon:{}", path, lastTime,bodyParams,result_body);
            return  result_body;
        } catch (IOException e) {
            long lastTime = System.currentTimeMillis()-firstTime2;
            log.error("getHttpPostResult {} error. request cost time:{}ms, request bodyParams:{}, errorMessage: {}", path, lastTime,bodyParams ,e.getMessage(), e);
        }
        return null;
    }


    /**
     * get 请求
     * @param url       请求url地址
     * @return string
     * */
    public static String doGet(String url) {
        return executeGet(url, null, null);
    }


    /**
     * get 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     * */
    public static String doGet(String url, Map<String, String> params) {
        return executeGet(url, params, null);
    }
//
//    /**
//     * get 请求
//     * @param url       请求url地址
//     * @param params    请求参数 map
//     * @return string
//     * */
//    public static ResponseResult doGetApi(String url, Map<String, String> params) {
//        Response response = excuteGet2(url, params, null);
//        try {
//            ResponseResult responseResult = new ResponseResult();
//            String body = response.body().string();
//
//            responseResult.setData(body);
//            responseResult.setCode(response.code());
//            responseResult.setSuccess(response.code() == 200);
//            responseResult.setRequestMethod(response.request().method());
//            responseResult.setUrl(response.request().url().toString());
//            return responseResult;
//        } catch (Exception e) {
//            log.error("catch exception: ", e);
//        } finally {
//            if (response != null) {
//                if(response.body() != null){
//                    response.body().close();
//                }
//                response.close();
//            }
//        }
//        return null;
//    }

    /**
     * get 请求
     * @param url       请求url地址
     * @param headers   请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     * */
    public String doGet(String url, String[] headers) {
        return executeGet(url, null, headers);
    }

    /**
     * get 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @param headers   请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     * */
    public static String executeGet(String url, Map<String, String> params, String[] headers) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && params.keySet().size() > 0) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                } else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }

        Request.Builder builder = new Request.Builder();
        if (headers != null && headers.length > 0) {
            if (headers.length % 2 == 0) {
                for (int i = 0; i < headers.length; i = i + 2) {
                    builder.addHeader(headers[i], headers[i + 1]);
                }
            } else {
                log.warn("headers' length error: " + headers.length);
            }

        }

        Request request = builder.url(sb.toString()).build();
//        log.info(String.format("send %s request to %s", request.method(), request.url()));
        return execute(request);
    }

    /**
     * post 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     */
    public static String doPost(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();

        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        log.info("do post request and url: " + url);

        return execute(request);
    }

    /**
     * 发送post请求通过JSON参数
     *
     * @param reqUrl 请求url
     * @param param  请求参数
     */
    public static JSONObject postByJson(String reqUrl, Object param) {
        JSONObject jsonObject = null;
        try {
            String paramStr = JSONUtil.toJsonStr(param);

            RequestBody requestBody = RequestBody.create(JSON, paramStr);
            long startTime = System.currentTimeMillis();

            Request.Builder builder = new Request.Builder().url(reqUrl).post(requestBody);
            String body = null;
            try (Response response = okHttpClient.newCall(builder.build()).execute()) {
                body = response.body().string();
                jsonObject = JSONUtil.parseObj(body);
            } catch (Exception e) {
                log.error("postByJson exception reqUrl:{} ,jsonObject:{}:",reqUrl,jsonObject,e);
            } finally {
                long endTime = System.currentTimeMillis();
//                log.info("OkHttpUtil:{} cost time:{}", reqUrl.substring(reqUrl.lastIndexOf("/") + 1),
//                        (endTime - startTime));
            }
        } catch (Exception e) {
            log.error("[postByJson:OkHttpUtil2]-[error:{}]", e);
        }
        return jsonObject;
    }

//    /**
//     * post 请求带param
//     * @param url       请求url地址
//     * @param params    请求参数 map
//     * @return ResponseResult 返回结果
//     */
//    public static ResponseResult doPostApiWithParam(String url, Map<String, String> params) {
//        FormBody.Builder builder = new FormBody.Builder();
//
//        if (params != null && params.keySet().size() > 0) {
//            for (String key : params.keySet()) {
//                builder.add(key, params.get(key));
//            }
//        }
//        Request request = new Request.Builder().url(url).post(builder.build()).build();
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            String body = response.body().string();
//            return JSONUtil.toBean(body, ResponseResult.class);
//        } catch (Exception e) {
//            log.error("doPostApiWithParam: catch exception: ", e);
//        } finally {
//            if (response != null) {
//                if(response.body() != null){
//                    response.body().close();
//                }
//                response.close();
//            }
//        }
//        return null;
//    }


    /**
     * post 请求, 请求数据为 json 的字符串
     * @param url       请求url地址
     * @param json      请求数据, json 字符串
     * @return string
     */
    public static String doPostJson(String url, String json) {
        return executePost(url, json, JSON);
    }

    /**
     * post 请求, 请求数据为 xml 的字符串
     * @param url       请求url地址
     * @param xml       请求数据, xml 字符串
     * @return string
     */
    public static String doPostXml(String url, String xml) {
        return executePost(url, xml, XML);
    }


    private static String executePost(String url, String data, MediaType contentType) {
        RequestBody requestBody = RequestBody.create(contentType, data);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        return execute(request);
    }

    private static String execute(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            if (response != null) {
                if(response.body() != null){
                    response.body().close();
                }
                response.close();
            }
        }
        return "";
    }
//    /**
//     * post 请求, 请求数据为 json 的字符串
//     * @param url       请求url地址
//     * @param json      请求数据, json 字符串
//     * @return string
//     */
//    public static ResponseResult doPostApi(String url, String json) {
//        RequestBody requestBody = RequestBody.create(JSON, json);
//        Request request = new Request.Builder().url(url).post(requestBody).build();
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            String body = response.body().string();
//            return JSONUtil.toBean(body, ResponseResult.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (response != null) {
//                if(response.body() != null){
//                    response.body().close();
//                }
//                response.close();
//            }
//        }
//        return null;
//    }
    /**
     * get 请求
     * @param url       请求url地址
     * @return string
     * */
    public static Response requestGet(String url) {
        return excuteGet2(url, null, null);
    }


    /**
     * get 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @return string
     * */
    public static Response requestGet(String url, Map<String, String> params) {
        return excuteGet2(url, params, null);
    }
    /**
     * get 请求
     * @param url       请求url地址
     * @param params    请求参数 map
     * @param headers   请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     * */
    public static Response excuteGet2(String url, Map<String, String> params, String[] headers) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && params.keySet().size() > 0) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                } else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }

        Request.Builder builder = new Request.Builder();
        if (headers != null && headers.length > 0) {
            if (headers.length % 2 == 0) {
                for (int i = 0; i < headers.length; i = i + 2) {
                    builder.addHeader(headers[i], headers[i + 1]);
                }
            } else {
//                log.warn("headers's length[{}] is error.", headers.length);
            }

        }

        Request request = builder.url(sb.toString()).build();
//        log.info(String.format("send %s request to %s", request.method(), request.url()));
        return executeRequest(request);
    }
    /**
     * post 请求, 请求数据为 xml 的字符串
     * @param request       请求
     * @return Response
     */
    private static Response executeRequest(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getClass() + ": " + e.getMessage());
        }
        return response;
    }
}

class RetryInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger("_LogMainSql_");

    public int maxRetry; // 最大重试次数
    private int retryNum = 0; // 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isSuccessful()) {
            return response;
        }
        while (!response.isSuccessful() && retryNum < maxRetry) {
            ResponseBody body = response.body();
            if (body != null) {
                log.error(body.string());
            }
            retryNum++;
            log.info("retryNum=" + retryNum);
            response = chain.proceed(request);
        }
        if (response.isSuccessful()) {
            log.info("retry success");
        } else {
            log.info("retry failed, body: " + response.body().string());
        }

        return response;
    }
}

