package com.pplive.liveplatform.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Use to read the config file in asserts/conf/config.properties
 * 
 */
public class ConfigUtil {
    private final static String CONFIG_FILE_PATH = "/assets/conf/config.properties";
    /**
     * Singleton pattern
     */
    private static ConfigUtil instance;
    private Properties config;

    private ConfigUtil() {
        loadConfig();
    }

    /**
     * Returns the singleton instance of ConfigUtil.
     * 
     * @return the instance
     */
    public static ConfigUtil getInstance() {
        if (instance == null) {
            synchronized (ConfigUtil.class) {
                if (instance == null) {
                    instance = new ConfigUtil();
                }
            }
        }
        return instance;
    }

    /**
     * Loads the default configuration file.
     */
    private void loadConfig() {
        config = new Properties();
        try {
            InputStream is = ConfigUtil.class.getResourceAsStream(CONFIG_FILE_PATH);
            config.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the loaded configuration object.
     * 
     * @return the configuration
     */
    private Properties getConfig() {
        return this.config;
    }

    /**
     * get the value of property
     * 
     * @param property
     *            The property
     * @return the value of property
     */
    public static String getString(String property) {

        return StringUtil.safeStringEmpty(ConfigUtil.getInstance().getConfig().getProperty(property));
    }

    /**
     * Use to search the config key
     * 
     * @param prefix
     *            The key prefix
     * @param postfix
     *            The key postfix
     * @return The key array
     */
    public static String[] searchKeys(String prefix) {
        List<String> keyList = new ArrayList<String>();
        Enumeration<Object> allKeys = ConfigUtil.getInstance().config.keys();

        while (allKeys.hasMoreElements()) {
            String key = allKeys.nextElement().toString();
            if (StringUtil.notNullOrEmpty(prefix)) {
                if (key.startsWith(prefix)) {
                    keyList.add(key);
                }
            }
        }

        String[] keys = new String[keyList.size()];
        keyList.toArray(keys);
        return keys;
    }
}
