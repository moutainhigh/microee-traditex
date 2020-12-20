package com.microee.traditex.inbox.up.jumptrading;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtil {

    public static final String ALGORITHM_SHA384 = "HmacSHA384";

    public static String encryptStr(String text, String key, String algorithm, String charset) throws Exception {
        byte[] data = text.getBytes(charset);
        return byteArrayToHexString(encrypt(data, key, algorithm));
    }

    public static String encryptStr(byte[] data, String key, String algorithm) throws Exception {
        return byteArrayToHexString(encrypt(data, key, algorithm));
    }

    public static byte[] encrypt(byte[] data, String key, String algorithm) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

}