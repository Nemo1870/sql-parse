package com.sql.parse.config;

import com.alibaba.druid.DbType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

public class SqlParseConfig {
    private static final Logger logger = LogManager.getLogger(SqlParseConfig.class);

    static final String DEFAULT_SQL_PARSE_CONFIG = "com/sql/parse/config/sql-parse";
    static final String DEFINE_SQL_PARSE_CONFIG = "sql-parse";
    private static String HANDLE;
    private static String DB_TYPE;


    static{
        try {
        	/*指定使用zh_CN*/
            ResourceBundle resourceBundle = ResourceBundle.getBundle(DEFAULT_SQL_PARSE_CONFIG.replaceAll("/", "."), new Locale("zh", "CN"));
            SqlParseConfig.HANDLE = resourceBundle.getString("handle");
            SqlParseConfig.DB_TYPE = resourceBundle.getString("db.type");
        } catch (Exception e) {
            logger.error("加载默认的配置文件出错：" + e.getMessage());
        }
        try {
            /*指定使用zh_CN*/
            ResourceBundle resourceBundle = ResourceBundle.getBundle(DEFINE_SQL_PARSE_CONFIG.replaceAll("/", "."), new Locale("zh", "CN"));
            SqlParseConfig.HANDLE = resourceBundle.getString("handle");
            SqlParseConfig.DB_TYPE = resourceBundle.getString("db.type");
        } catch (Exception e) {
            logger.debug("没有指定sql-parse的自定义配置文件");
        }
    }

    public static String getHandle() {
    	return HANDLE;
    }

    public static DbType getDbType() {
        return DbType.of(DB_TYPE);
    }
}
