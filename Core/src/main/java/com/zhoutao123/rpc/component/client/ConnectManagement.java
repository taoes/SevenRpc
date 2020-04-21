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

  public void connectServerNode(List<InetSocketAddress> addresses) {}

  /** 连接到服务器 */
  private void connectServerNode(final InetSocketAddress address) throws InterruptedException {}

  /** 新增处理器 */
  public void addHandler(RpcClientHandler handler) {}

  public RpcClientHandler chooseHandler() {
    return null;
  }
}
