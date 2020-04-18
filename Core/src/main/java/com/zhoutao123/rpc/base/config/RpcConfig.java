package com.zhoutao123.rpc.base.config;

import com.zhoutao123.rpc.utils.PropertiesUtils;
import java.util.Set;
import org.springframework.context.annotation.Configuration;

/** SevenRPC 服务配置信息 */
@Configuration
public class RpcConfig {

  private static final String PROPERTIES_PATH = "/rpc.properties";

  private static PropertiesUtils properties = PropertiesUtils.getInstance(PROPERTIES_PATH);

  public static Integer SERVICE_PORT = properties.getInteger("port", 7777);

  public static String APPLICATION_NAME = properties.getString("name");

  public static final String BEAN_SCAN_PACKAGE = properties.getString("package");

  // 自定义的扫描路径
  public Set<String> getCustomPackage() {
    return null;
  }
}
