package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.component.client.RpcFuture;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.service.netty.client.RpcClientHandler;
import com.zhoutao123.rpc.utils.ClassUtils;
import com.zhoutao123.rpc.utils.HashUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/** 生成消费者的代理对象 */
public class NettyProxyHandler implements InvocationHandler {

  public Object bind(Class<?> cls) {
    return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    // 判断此方法是否是Object的默认方法，值得话直接执行并返回
    boolean contains = ClassUtils.allMethodNameOfClass(Object.class).contains(method.getName());
    if (contains) {
      return method.invoke(method, args);
    }

    // 生成请求对象
    RpcRequest request = generator(method, args);
    // 获取处理器
    RpcClientHandler handled = getHandled();

    // 发送请求 & 返回请求的Future对象
    RpcFuture future = handled.sendRequest(request);
    return future.get();
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

  private RpcClientHandler getHandled() throws InterruptedException {
    // 负载均衡算法
    return ConnectManagement.getInstance().connectServerNode(null);
  }
}
