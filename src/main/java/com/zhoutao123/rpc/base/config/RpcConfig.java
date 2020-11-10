package com.zhoutao123.rpc.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** SevenRPC 服务配置信息 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.seven-rpc")
public class RpcConfig {

  private String name = "";

  private int port = 7077;

  private final int timeout = 5;

  private ZkConfig zk = new ZkConfig();
}
