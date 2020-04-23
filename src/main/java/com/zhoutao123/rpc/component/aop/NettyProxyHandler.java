package com.zhoutao123.rpc.component.aop;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.exception.RpcBizException;
import com.zhoutao123.rpc.base.exception.RpcTimeoutException;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.entity.RpcResponse;
import com.zhoutao123.rpc.service.netty.client.RpcClientHandler;
import com.zhoutao123.rpc.service.netty.client.RpcClientInitializer;
import com.zhoutao123.rpc.utils.ClassUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

  private Log log = LogFactory.get();

  private static final EventLoopGroup group = new NioEventLoopGroup(4);

  public Object bind(Class<?> cls) {
    return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    // 如果是Object的方法直接返回空
    boolean contains = ClassUtils.allMethodNameOfClass(Object.class).contains(method.getName());
    if (contains) {
      return method.invoke(method, args);
    }

    log.trace("进入代理方法:{}", method.getName());

    final CountDownLatch latch = new CountDownLatch(1);
    RpcRequest request = generator(method, args);

    Bootstrap b = new Bootstrap();
    RpcClientInitializer rpcClientInitializer = new RpcClientInitializer(latch);

    ChannelFuture sync =
        b.group(group)
            .channel(NioSocketChannel.class)
            .handler(rpcClientInitializer)
            .connect(new InetSocketAddress("127.0.0.1", 8888))
            .sync();

    Channel channel = sync.channel();
    RpcClientHandler clientHandler = channel.pipeline().get(RpcClientHandler.class);

    clientHandler.send(request);

    // 等待请求完成
    boolean await = latch.await(10, TimeUnit.SECONDS);
    if (!await) {
      throw new RpcTimeoutException("请求 rpc 方法超时");
    }

    channel.close().sync();

    RpcResponse response = clientHandler.response;

    if (response == null) {
      throw new RpcBizException("请求失败");
    }

    if (response.getError() != null) {
      throw new RpcBizException(response.getError());
    }
    return response.getResult();
  }

  /** 生成请求对象 */
  private RpcRequest generator(Method method, Object[] args) {
    RpcRequest rpcRequest = new RpcRequest();
    String requestId = UUID.randomUUID().toString();
    rpcRequest.setRequestId(requestId);
    rpcRequest.setParameters(args);
    rpcRequest.setMethodName(method.toGenericString());
    rpcRequest.setParameterTypes(method.getParameterTypes());
    return rpcRequest;
  }
}
