package com.kaishengit.util;

import java.io.IOException;
import java.util.Properties;
/**
 * Created by jiahao0 on 2016/12/16.
 */
public class Config {

    private static Properties properties = new Properties();

    //设置静态代码块
    static {
        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("读取config.properties文件异常",e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
