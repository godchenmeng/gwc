package com.youxing.car.utils.other;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class Encryption {
    private static String KEY = "yxcl";
    private static String ALGORITHM = "AES";

    public Encryption() {
    }

    public static String encrypt(String content) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(KEY.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(1, key);
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String content) {
        try {
            byte[] decryptFrom = parseHexStr2Byte(content);
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(KEY.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, key);
            byte[] result = cipher.doFinal(decryptFrom);
            return new String(result);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    private static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < buf.length; ++i) {
            String hex = Integer.toHexString(buf[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte)(high * 16 + low);
            }

            return result;
        }
    }
}
