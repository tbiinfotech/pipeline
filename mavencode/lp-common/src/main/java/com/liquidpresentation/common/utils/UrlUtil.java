package com.liquidpresentation.common.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UrlUtil {

    private static final String KEY = "myMw6qP&3DKY";
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlUtil.class);

/*    public static void main(String[] args) throws Exception {
        String source = UUID.randomUUID().toString() + "_" + "123456@qq.com";
        String result = enCryptAndEncode(source);
        System.out.println("after encrypt:" + result);
        String source_2 = deCryptAndDecode(result);
        System.out.println("Actual:" + source_2);

        String isSuccess = source.equals(source_2) ? "Success" : "fail";
        System.out.println("Result:" + isSuccess);
    }*/

    public static String enCryptAndEncode(String content) {
        try {
            byte[] sourceBytes = enCryptAndEncode(content, KEY);
            return Base64.encodeBase64URLSafeString(sourceBytes);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return content;
        }
    }

    public static byte[] enCryptAndEncode(String content, String strKey) throws Exception {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, new SecureRandom(strKey.getBytes()));

        SecretKey desKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        return cipher.doFinal(content.getBytes("UTF-8"));
    }

    public static String deCryptAndDecode(String content) throws Exception {
        byte[] targetBytes = Base64.decodeBase64(content);
        return deCryptAndDecode(targetBytes, KEY);
    }


    public static String deCryptAndDecode(byte[] src, String strKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, new SecureRandom(strKey.getBytes()));

        SecretKey desKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, desKey);
        byte[] cByte = cipher.doFinal(src);
        return new String(cByte, "UTF-8");
    }

}