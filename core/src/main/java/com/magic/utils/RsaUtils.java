package com.magic.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sevenmagicbeans
 * @date 2022/12/21
 */
public class RsaUtils {



    private static String publicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAng4BGKfe4fgmMexL2GcZ" +
            "tISaAXsm1EZ0ub4Grm+OVI0muEc6tjWg0S07a5SuOOF0CjhHCVmjpIRYJaumhL+0" +
            "delvEewICWy4Ja23ep9/wFbwYO1qBI4KVU/Ck7Nfi//yJzYfcik/4Sp5ZVTJTAf0" +
            "aYfv/BdCnVJV6Kjdt1V3J05qA+0T0sRNNctsTnb+OorP4ANoP8S4BARnPyH9qTZe" +
            "oEMFgFMkCg9nut4xw9q4DuNMflb6BkKR9bzI7hvP/DeuDunUezLCqpX7V4j/k0Ez" +
            "w+oU6tZtJWNT/hEDjZkSZWHWH+Lq8ULZyFfpLCJNti8cp7FRTF2JD1i4UQfzsZ9u" +
            "/QIDAQAB";
    

    private static String privateKey ="";


    public static void main(String[] args) throws Exception {
        Map<String,Object> newMap = new HashMap<>();
        newMap.put("member_id",100292715453600L);
        newMap.put("login_time",1666093895);
        newMap.put("login_ip","127.0.0.1");
        newMap.put("platform","web");


        String enStr =  encrypt(publicKey,new ObjectMapper().writeValueAsString(newMap));
        //OkhttpUtil.postByJson("https://apick.tp-serv.com/user/login",enStr);
        String deStr =  decrypt(privateKey,enStr);
    }
    public static Map<String,String> encryptMapToPayload(String publicKey,Map parmMap) throws Exception {
        Map newMap = new HashMap<String,String>();
        newMap.put("payload",encrypt(publicKey, new ObjectMapper().writeValueAsString(parmMap)));
        return   newMap ;
    }

    public static String encrypt(String publicKey,String text) throws Exception {
        System.out.println("before encrypt source text：" + text);
        RSA rsa = new RSA(null, publicKey);
        byte[] encrypt = rsa.encrypt(StrUtil.bytes(text, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        String result = bytesToBase64(encrypt);
        System.out.println("after rsa encrypt：" + result);
        return result;
    }


    public static String decrypt(String privateKey,String text) throws Exception {

        System.out.println("before decrypt source text：" + text);

        RSA rsa = new RSA(privateKey, null);


        byte[] decode64Bytes = base64ToBytes(text);
        byte[] decrypt = rsa.decrypt(decode64Bytes, KeyType.PrivateKey);
        String result = new String(decrypt, StandardCharsets.UTF_8);
        System.out.println("after rsa decrypt：" + result);
        return result;
    }

    /**
     * 字节数组转Base64编码
     *
     * @param bytes 字节数组
     * @return Base64编码
     */
    private static String bytesToBase64(byte[] bytes) {
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64编码转字节数组
     *
     * @param base64Str Base64编码
     * @return 字节数组
     */
    private static byte[] base64ToBytes(String base64Str) {
        byte[] bytes = base64Str.getBytes(StandardCharsets.UTF_8);
        return Base64.getDecoder().decode(bytes);
    }
}
