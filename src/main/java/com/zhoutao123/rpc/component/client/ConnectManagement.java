package com.zhoutao123.rpc.component.client;

import com.zhoutao123.rpc.service.netty.client.RpcClientHandler;
import com.zhoutao123.rpc.service.netty.client.RpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/** Netty 连接管理器 */
@Slf4j
public class ConnectManagement {

  private static volatile ConnectManagement management;

  private final EventLoopGroup group =
      new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

  private static final ConcurrentHashMap<String, RpcClientHandler> handlerMap =
      new ConcurrentHashMap<>();

  /** 获取连接实例 */
  public static ConnectManagement getInstance() {
    if (management == null) {
      management = new ConnectManagement();
    }
    return management;
  }

  /** 连接地址 */
  public RpcClientHandler connectServerNode(SocketAddressWrapper address)
      throws InterruptedException {
    String addressId = address.getAddressId();
    RpcClientHandler handler = handlerMap.getOrDefault(addressId, new RpcClientHandler());

    Bootstrap b = new Bootstrap();
    RpcClientInitializer rpcClientInitializer = new RpcClientInitializer(handler);
    b.group(group)
        .channel(NioSocketChannel.class)
        .handler(rpcClientInitializer)
        .connect(address)
        .sync();
    handlerMap.put(addressId, handler);
    return rpcClientInitializer.getHandler();
  }
}
