package com.pplive.liveplatform.util;

import java.io.File;

public class FileUtil {

    public static void checkPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
