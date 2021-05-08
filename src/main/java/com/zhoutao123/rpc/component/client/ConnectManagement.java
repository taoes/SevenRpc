package com.zhoutao123.rpc.component.client;

import com.zhoutao123.rpc.client.netty.RpcClientHandler;
import com.zhoutao123.rpc.client.netty.RpcClientInitializer;
import com.zhoutao123.rpc.entity.NodeInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/** Netty 连接管理器 */
@Slf4j
public class ConnectManagement {

  private static volatile ConnectManagement management;

  private final EventLoopGroup group =
      new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

  private static final ConcurrentHashMap<Integer, RpcClientHandler> handlerMapOfAddHash =
      new ConcurrentHashMap<>();

  private static final Map<String, List<RpcClientHandler>> handlerMapOfMethod =
      new ConcurrentHashMap<>();

  /** 获取连接实例 */
  public static ConnectManagement getInstance() {
    if (management == null) {
      synchronized (ConnectManagement.class) {
        if (management == null) {
          management = new ConnectManagement();
        }
      }
    }
    return management;
  }

  /** 初始化连接 */
  public void connectServerNode(String method, NodeInfo nodeInfo) throws InterruptedException {

    if (!StringUtils.hasText(method)) {
      throw new RuntimeException("获取连接失败,连接方法名不存在");
    }

    Integer addressHash = nodeInfo.getAddressId();
    if (handlerMapOfAddHash.containsKey(addressHash)) {
      RpcClientHandler handler = handlerMapOfAddHash.get(addressHash);
      List<RpcClientHandler> handlers = handlerMapOfMethod.getOrDefault(method, new ArrayList<>());
      handlers.add(handler);
      handlerMapOfMethod.put(method, handlers);
      return;
    }

    Bootstrap b = new Bootstrap();
    RpcClientHandler handler =
        handlerMapOfAddHash.getOrDefault(addressHash, new RpcClientHandler());
    RpcClientInitializer rpcClientInitializer = new RpcClientInitializer(handler);
    b.group(group)
        .channel(NioSocketChannel.class)
        .handler(rpcClientInitializer)
        .connect(SocketAddressWrapper.getInstance(nodeInfo))
        .sync();
    handlerMapOfAddHash.put(addressHash, handler);

    List<RpcClientHandler> handlers = handlerMapOfMethod.getOrDefault(method, new ArrayList<>());
    handlers.add(handler);
    handlerMapOfMethod.put(method, handlers);
  }

  public RpcClientHandler getConnectByMethod(String method) {
    List<RpcClientHandler> rpcClientHandlers = handlerMapOfMethod.get(method);
    return rpcClientHandlers.get(0);
  }

  /** 断开连接 */
  public int disconnect(String method) {
    List<RpcClientHandler> handlers =
        handlerMapOfMethod.getOrDefault(method, Collections.emptyList());
    for (RpcClientHandler handler : handlers) {
      handler.closeClient();
    }
    handlerMapOfMethod.remove(method);
    return handlers.size();
  }

  /** 重新连接 */
  public void reconnect() {}
}
