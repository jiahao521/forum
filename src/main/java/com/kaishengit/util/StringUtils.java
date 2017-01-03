package com.kaishengit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
/**
 * Created by jiahao0 on 2016/12/16.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    //日志
    private static Logger logger = LoggerFactory.getLogger(StringUtils.class);

    //将ISO8859-1转换为UTF-8
    public static String isoToUtf8(String str) {

        try {
            return new String(str.getBytes("ISO8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {

            logger.error("字符串{}转换异常",str);
            throw new RuntimeException("字符串" + str + "转换异常",e);
        }
    }

}
