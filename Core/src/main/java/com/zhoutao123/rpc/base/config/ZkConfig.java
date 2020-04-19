package com.zhoutao123.rpc.base.config;

import lombok.Data;

/**
 * SevenRPC 服务配置信息
 */
@Data
public class ZkConfig {

  private String host = "127.0.0.1";

  private Integer sessionTimeout = 6000;

  private Integer port = 2181;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getSessionTimeout() {
    return sessionTimeout;
  }

  public void setSessionTimeout(Integer sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }
}
