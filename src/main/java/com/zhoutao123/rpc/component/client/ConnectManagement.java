package com.zhoutao123.rpc.component.client;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.service.netty.client.RpcClientHandler;
import com.zhoutao123.rpc.service.netty.client.RpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty 连接管理器
 *
 * @since 0.0.01
 */
public class ConnectManagement {

  private static final Log log = LogFactory.get();

  // 地址信息
  private Map<InetSocketAddress, RpcClientHandler> handlerMap = new ConcurrentHashMap<>();

  private static volatile ConnectManagement management;

  private EventLoopGroup group =
      new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

  public static ConnectManagement getInstance() {
    if (management == null) {
      management = new ConnectManagement();
    }
    return management;
  }

  /**
   * 连接地址
   *
   * @param address 连接的地址信息
   */
  public void connectServerNode(List<InetSocketAddress> address) throws InterruptedException {
    Bootstrap b = new Bootstrap();
    RpcClientInitializer rpcClientInitializer = new RpcClientInitializer(null);

    b.group(group)
        .channel(NioSocketChannel.class)
        .handler(rpcClientInitializer)
        .connect(new InetSocketAddress("127.0.0.1", 8888))
        .sync();
  }
}
