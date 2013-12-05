package com.pplive.liveplatform.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Base64;

public class AesUtil {
    private static final String ALGORITHM = "AES";

    public static String encrypt(String cleartext, Context context) {
        return encrypt(cleartext, ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
    }

    public static String encrypt(String cleartext, String seed) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] result = encrypt(rawKey, cleartext.getBytes("utf-8"));
            return Base64.encodeToString(result, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encrypted, Context context) {
        return decrypt(encrypted, ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
    }

    public static String decrypt(String encrypted, String seed) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] enc = Base64.decode(encrypted, Base64.DEFAULT);
            byte[] result = decrypt(rawKey, enc);
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
}