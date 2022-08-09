package com.sql.parse.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(BaseException.class);

    private static Map<String, String> codeMap = new HashMap<>();

    private BaseException(String message) {
        super(message);
    }

    private BaseException(Throwable cause) {
        super(cause);
    }

    private BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BaseException errorCode(String code) throws BaseException {
         String message = codeMap.get(code);
         if (message == null) {
             throw new BaseException("错误编码【" + code + "】未定义。");
         }
         return new BaseException(message);
    }

    public static BaseException errorCode(String code, String... params) throws BaseException {
        String message = codeMap.get(code);
        if (message == null) {
            throw new BaseException("错误编码【" + code + "】未定义。");
        }
        message = bind(message, params);
        return new BaseException(message);
    }

    private static String bind(String s, String... binds) {
        if (binds != null && binds.length > 0) {
            Pattern pattern = Pattern.compile("%v", 34);
            StringBuffer buff = new StringBuffer();
            Matcher m = pattern.matcher(s);
            int i = 0;

            for(int len = binds.length; i < len && m.find(); ++i) {
                m.appendReplacement(buff, binds[i]);
            }

            m.appendTail(buff);
            return buff.toString();
        } else {
            return s;
        }
    }

    public static BaseException errorCode(String code, Throwable cause) throws BaseException {
        String message = codeMap.get(code);
        if (message == null) {
            throw new BaseException("错误编码【" + code + "】未定义。");
        }
        return new BaseException(message, cause);
    }

    public static BaseException error(Throwable cause) {
        return new BaseException(cause);
    }

    public static void register(String code, String message) throws BaseException {
        if (codeMap.containsKey(code)) {
            throw new BaseException("错误编码【" + code + "】已定义。");
        }
        codeMap.put(code, message);
    }

    public static void register(String file) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(file.replaceAll("/", "."), new Locale("zh", "CN"));
        for (String key : resourceBundle.keySet()) {
            try {
                String value = new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
                BaseException.register(key, value);
            } catch (BaseException e) {
                logger.error(e.getMessage() + "已忽略该错误码。");
            } catch (UnsupportedEncodingException e) {
                logger.error("错误码【" + key + "】的信息转码错误。已忽略该错误码。");
            }
        }
    }
}
