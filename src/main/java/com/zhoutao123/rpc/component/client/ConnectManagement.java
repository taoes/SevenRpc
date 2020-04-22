package com.zhoutao123.rpc.component.client;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.service.netty.client.RpcClientHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectManagement {

  private static final Log log = LogFactory.get();

  // handler处理器
  private List<RpcClientHandler> rpcClientHandler = new ArrayList<>();

  // 地址信息
  private Map<InetSocketAddress, RpcClientHandler> handlerMap = new ConcurrentHashMap<>();

  // 轮询计数器
  private AtomicInteger roundRobin = new AtomicInteger(0);

  private static volatile ConnectManagement management;

  private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

  public static ConnectManagement getInstance() {
    if (management == null) {
      management = new ConnectManagement();
    }
    return management;
  }

  /**
   * 连接地址
   *
   * @param addresses 连接的地址信息
   */
  public void connectServerNode(List<InetSocketAddress> addresses) {}
}
