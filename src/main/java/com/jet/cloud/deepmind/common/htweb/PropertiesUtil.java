package com.jet.cloud.deepmind.common.htweb;


import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Properties;

/**
 * 系统属性工具
 */
public class PropertiesUtil {

    /**
     * 系统配置信息
     */
    private static Properties systemConfig;

    /**
     * 开放配置
     */
    private static Properties openConfig;

    public static String basePath;

    private final static String PATH_PROP = "application.properties";

//    /**
//     * 日志处理
//     */
//    private static Logger log = Logger.getLogger(PropertiesUtil.class);

    /**
     * 禁止工具类实例化
     */
    private PropertiesUtil() {

    }

    static {

        InputStream inputStream = null;
        InputStream openConfigStream = null;
        try {
            // inputStream = new FileInputStream(path);
            inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PATH_PROP);
            systemConfig = new Properties();
            systemConfig.load(inputStream);

            File file = new File(PATH_PROP);
            if (file.exists()) {
                FileInputStream fs = new FileInputStream(file);
                openConfigStream = new BufferedInputStream(fs);
                openConfig = new Properties();
                openConfig.load(openConfigStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (openConfigStream != null) {
                    openConfigStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param key 键
     * @return 值
     * @Description: 获取指定系统属性
     * @date: 2015年8月26日 下午7:55:19
     */
    public static String getProperties(String key) {
        // 先获取用户自定义属性，获取不到再获取默认配置
        if (openConfig != null && !StringUtils.isEmpty(openConfig.getProperty(key))) {
            return openConfig.getProperty(key);
        } else {
            return systemConfig.getProperty(key);
        }
    }

}

