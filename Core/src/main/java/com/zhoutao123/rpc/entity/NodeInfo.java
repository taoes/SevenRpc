package com.zhoutao123.rpc.entity;

import java.io.Serializable;

/** 方法信息实体 */
public class NodeInfo implements Serializable {

  private final String ip;

  private final Integer port;

  public NodeInfo(String ip, Integer port) {
    this.ip = ip;
    this.port = port;
  }

  public String getIp() {
    return ip;
  }

  public Integer getPort() {
    return port;
  }
}
