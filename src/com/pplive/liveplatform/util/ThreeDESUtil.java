package com.pplive.liveplatform.util;

import java.security.InvalidParameterException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class ThreeDESUtil {

    private static final String KEY[] = { "70706C6976656F6B", "15B9FDAEDA40F86BF71C73292516924A294FC8BA31B6E9EA",
            "29028A7698EF4C6D3D252F02F4F79D5815389DF18525D326", "D046E6B6A4A85EB6C44C73372A0D5DF1AE76405173B3D5EC",
            "435229C8F79831131923F18C5DE32F253E2AF2AD348C4615", "9B2915A72F8329A2FE6B681C8AAE1F97ABA8D9D58576AB20",
            "B3B0CD830D92CB3720A13EF4D93B1A133DA4497667F75191", "AD327AFB5E19D023150E382F6D3B3EB5B6319120649D31F8",
            "C42F31B008BF257067ABF115E0346E292313C746B3581FB0", "529B75BAE0CE2038466704A86D985E1C2557230DDF311ABC",
            "8A529D5DCE91FEE39E9EE9545DF42C3D9DEC2F767C89CEAB", };

    private static final String CRYPT_ALGORITHM = "DESede/CBC/PKCS5Padding";

    private static final String ENCODING_TYPE = "UTF-8";

    private static final byte DEFAULT_IV[] = { 1, 2, 3, 4, 5, 6, 7, 8 };

    private static final String KEY_ALGORITHM = "DESede";

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String encode(String param, int keyIndex) throws EncryptException {
        return encode(param, keyIndex, KEY[0]);
    }

    public static String encode(String param, int keyIndex, String hexIv) throws EncryptException {
        if (keyIndex <= 0 || keyIndex >= KEY.length) {
            throw new InvalidParameterException();
        }

        try {
            String key = KEY[keyIndex];
            byte[] byteIV = hex2byte(hexIv);
            byte input[] = Hex.decode(key);
            Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            DESedeKeySpec desKeySpec = new DESedeKeySpec(input);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivGenerator(byteIV));

            byte[] output = cipher.doFinal(param.getBytes(ENCODING_TYPE));
            String strDesEnc = new String(Base64.encode(output), ENCODING_TYPE);

            return strDesEnc;
        } catch (Exception e) {
            throw new EncryptException();
        }
    }

    public static String Decode(String param, int keyIndex) throws Exception {
        if (keyIndex <= 0 || keyIndex >= KEY.length) {
            throw new InvalidParameterException();
        }
        String key = KEY[keyIndex];
        byte[] byteIV = hex2byte(KEY[0]);
        String reponseDecrpt = decrypt(param, key, byteIV);

        return reponseDecrpt;
    }

    public static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    private static Key keyGenerator(String keyStr) throws Exception {
        byte input[] = Hex.decode(keyStr);
        DESedeKeySpec keySpec = new DESedeKeySpec(input);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generateSecret(keySpec);
    }

    private static IvParameterSpec ivGenerator(byte b[]) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(b);
        return iv;
    }

    private static byte[] base64Decode(String s) throws Exception {
        return Base64.decode(s);
    }

    private static String decrypt(String strTobeDeCrypted, String strKey, byte byteIV[]) throws Exception {
        byte input[] = base64Decode(strTobeDeCrypted);
        Key k = keyGenerator(strKey);
        IvParameterSpec ivSpec = byteIV.length != 0 ? ivGenerator(byteIV) : ivGenerator(DEFAULT_IV);
        Cipher c = Cipher.getInstance(CRYPT_ALGORITHM);
        c.init(2, k, ivSpec);
        byte output[] = c.doFinal(input);
        return new String(output, ENCODING_TYPE);
    }

    private static byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }

        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = Integer.valueOf(byteint).byteValue();
        }
        return b;
    }

    public static class EncryptException extends Exception {

        private static final long serialVersionUID = -8079168419396497666L;

        public EncryptException() {
            super();
        }

        public EncryptException(Throwable t) {
            super(t);
        }
    }
}
