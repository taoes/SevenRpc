package com.zhoutao123.rpc.utils;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件解析器
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @version 0.0.1
 * @data 2019-03-09 18:21
 */
public class PropertiesUtils {

  private static Log log = LogFactory.get();

  private static final String RPC_CONFIG_PREFIX = "application.rpc.";

  private PropertiesUtils() {}

  private static Map<String, PropertiesUtils> propertiesUtilsMap;

  private static Map<PropertiesUtils, Properties> propertiesMap;

  private Boolean loaded = Boolean.FALSE;

  static {
    propertiesUtilsMap = new HashMap<>();
    propertiesMap = new HashMap<>();
  }

  public static synchronized PropertiesUtils getInstance(String propertiesPath) {
    // 尝试从Map中获取
    PropertiesUtils utils = propertiesUtilsMap.get(propertiesPath);
    if (utils != null) {
      return utils;
    }
    // 如果不存在，尝试初始化
    Properties properties = null;
    try (InputStream stream = propertiesPath.getClass().getResourceAsStream(propertiesPath)) {
      properties = new Properties();
      if (stream != null) {
        properties.load(new InputStreamReader(stream));
        utils = new PropertiesUtils();
        propertiesUtilsMap.put(propertiesPath, utils);
        propertiesMap.put(utils, properties);
        utils.loaded = Boolean.TRUE;
      }
    } catch (IOException e) {
      log.error("初始化配置文件发现异常");
      System.exit(404);
    }
    return utils;
  }

  public String getString(String key) {
    if (loaded) {
      Object value = propertiesMap.get(this).get(RPC_CONFIG_PREFIX + key);
      return value == null ? "" : value.toString();
    }
    return null;
  }

  public Boolean getBoolean(String key) {
    String value = getString(key);
    return Boolean.TRUE.toString().equalsIgnoreCase(value);
  }

  public Integer getInteger(String key, Integer defaultValue) {
    String value = getString(key);
    try {
      return Integer.parseInt(value);
    } catch (Throwable e) {
      return defaultValue;
    }
  }
}
