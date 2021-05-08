package com.zhoutao123.rpc.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** SevenRPC 服务配置信息 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.seven-rpc")
public class RpcConfig {

  public static class DefaultConfig {
    public static final int port = 7077;
  }

  private String name = "";

  private int port = DefaultConfig.port;

  private final int timeout = 5;

  private ZkConfig zk = new ZkConfig();
}
