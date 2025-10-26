package com.magic.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;


/**
 * AESUtil
 *
 * @author sevenmagicbeans
 * @date 2022/12/21
 */
@Slf4j
public class AESCBCUtil {
    private static final String KEY = "368d2f287889f853";

    public static void main(String[] args) throws Exception {

        String contents = "k25zps8g34@nqmo.com";
        String encrypt = encrypt(contents,KEY);
        System.out.println("encrypt after:  " + encrypt);
        String decrypt = decrypt(encrypt,KEY);
        System.out.println("decrypt after:  " + decrypt);
    }

    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_NAME = "AES";
    // 加密模式
    public static final String ALGORITHM = "AES/CBC/PKCS7Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encrypt(String content,String key) {
        if(StrUtil.isBlank(content) || StrUtil.isBlank(key)){
            return  null;
        }
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(KEY.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception e) {
          log.error("aes encrypt error ",e);
        }
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content,String key) {
        if(StrUtil.isBlank(content) || StrUtil.isBlank(key)){
            return  null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(KEY.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal( Base64.getDecoder().decode(content)), CHARSET_NAME);
        } catch (Exception e) {
            log.error("aes decrypt error ",e);
        }
        return null;
    }





}
