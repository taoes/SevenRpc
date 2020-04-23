package com.zhoutao123.rpc.component.client;

import com.zhoutao123.rpc.base.exception.RpcParamException;
import com.zhoutao123.rpc.entity.NodeInfo;
import java.net.InetSocketAddress;

public class SocketAddressWrapper extends InetSocketAddress {

  private SocketAddressWrapper(String address, int port) {
    super(address, port);
  }

  public static SocketAddressWrapper getInstance(NodeInfo nodeInfo) {
    if (nodeInfo == null) {
      throw new RpcParamException("获取服务端信息异常，无法注册服务");
    }
    return new SocketAddressWrapper(nodeInfo.getIp(), nodeInfo.getPort());
  }
}
