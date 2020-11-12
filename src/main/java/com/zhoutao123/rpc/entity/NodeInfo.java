package com.zhoutao123.rpc.entity;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Objects;

/** 节点信息实体 */
public class NodeInfo implements Serializable {

  private static final long serialVersionUID = 2780021548824388469L;

  private final String ip;

  private final Integer port;

  public NodeInfo(String ip, Integer port) {
    this.ip = ip;
    this.port = port;
  }

  public String getIp() {
    if (this.ip == null) {
      return null;
    }
    return ip.toUpperCase();
  }

  public Integer getPort() {
    return port;
  }

  public InetSocketAddress toAddress() {
    return new InetSocketAddress(ip, port);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeInfo nodeInfo = (NodeInfo) o;
    return Objects.equals(getIp(), nodeInfo.getIp())
        && Objects.equals(getPort(), nodeInfo.getPort());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getIp(), getPort());
  }
}
