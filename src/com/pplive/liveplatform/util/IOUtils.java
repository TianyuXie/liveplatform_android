package com.pplive.liveplatform.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    private static final int BUFFER_SIZE = 4 * 1024;

    public static byte[] inputStream2Bytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] temp = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = is.read(temp, 0, BUFFER_SIZE)) != -1) {
            baos.write(temp, 0, len);
        }
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }
}
