package com.zhoutao123.rpc.client;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.exception.RpcBizException;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.entity.RpcResponse;
import com.zhoutao123.rpc.service.netty.client.RpcClientInitializer;
import com.zhoutao123.rpc.utils.ClassUtils;
import com.zhoutao123.rpc.utils.HashUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** 生成消费者的代理对象 */
public class NettyProxyHandler implements InvocationHandler {

  private final Log log = LogFactory.get();

  // 请求客户端的时间循环组
  private static final EventLoopGroup group = new NioEventLoopGroup(4);

  public Object bind(Class<?> cls) {
    return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    boolean contains = ClassUtils.allMethodNameOfClass(Object.class).contains(method.getName());
    if (contains) {
      return method.invoke(method, args);
    }

    final CountDownLatch latch = new CountDownLatch(1);
    RpcRequest request = generator(method, args);

    Bootstrap b = new Bootstrap();
    RpcClientInitializer rpcClientInitializer = new RpcClientInitializer(latch);

    // FIXME get Channel if context has validate channel

    ChannelFuture future =
        b.group(group)
            .channel(NioSocketChannel.class)
            .handler(rpcClientInitializer)
            .connect(new InetSocketAddress("127.0.0.1", 8889))
            .sync();

    future.channel().writeAndFlush(request);

    // Wait request is done ...
    boolean await = latch.await(10, TimeUnit.SECONDS);
    if (!await) {
      log.error("Error: rpc request is timeout");
      return null;
    }

    RpcResponse response = rpcClientInitializer.getHandler().getResponse();
    future.channel().close();
    if (response == null) {
      throw new RpcBizException("Request is fail");
    }

    if (response.getError() != null) {
      throw new RpcBizException(response.getError());
    }
    return response.getResult();
  }

  /** Get request info */
  private RpcRequest generator(Method method, Object[] args) {
    RpcRequest rpcRequest = new RpcRequest();
    String requestId = UUID.randomUUID().toString();
    rpcRequest.setRequestId(requestId);
    rpcRequest.setParameters(args);
    rpcRequest.setMethodName(HashUtils.md5(method.toGenericString()));
    rpcRequest.setParameterTypes(method.getParameterTypes());
    return rpcRequest;
  }
}
