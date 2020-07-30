package com.zhoutao123.rpc.base.config;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** SevenRPC 服务配置信息 */
@Configuration
@ConfigurationProperties(prefix = "application.seven-rpc")
public class RpcConfig {

  private String name = "";

  private int port = 7077;

  private final int timeout = 5;

  private ZkConfig zk = new ZkConfig();

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ZkConfig getZk() {
    return zk;
  }

  public void setZk(ZkConfig zk) {
    this.zk = zk;
  }

  public Set<String> getCustomPackage() {
    return new HashSet<>(0);
  }
}
