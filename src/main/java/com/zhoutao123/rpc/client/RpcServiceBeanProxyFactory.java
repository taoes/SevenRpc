package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.component.client.RpcFuture;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.client.netty.RpcClientHandler;
import com.zhoutao123.rpc.utils.ClassUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

/** RPC 客户端代理类生成工厂 */
@Data
@SuppressWarnings("ALL")
public class RpcServiceBeanProxyFactory<T> implements FactoryBean<T> {

  private Class<T> interfaceClass;

  @Override
  public T getObject() {
    NettyProxyHandler instance = new NettyProxyHandler();
    return (T) instance.bind(interfaceClass);
  }

  @Override
  public Class<?> getObjectType() {
    return interfaceClass;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  /** 代理类 */
  static class NettyProxyHandler implements InvocationHandler {

    public Object bind(Class<?> cls) {
      return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, this);
    }

    /** 代理类方法执行逻辑 */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

      // 判断此方法是否是Object的方法，是的话直接执行并返回不走代理
      boolean isBaseFunc = ClassUtils.allMethodNameOfClass(Object.class).contains(method.getName());
      if (isBaseFunc) {
        return method.invoke(method, args);
      }

      // 生成请求对象
      RpcRequest request = generator(method, args);
      RpcClientHandler handled = getHandled(method.toGenericString());

      // 发送请求 & 返回请求的Future对象
      RpcFuture future = handled.sendRequest(request);
      return future.get();
    }

    /** 生成 RpcRequest 对象 */
    private RpcRequest generator(Method method, Object[] args) {
      return new RpcRequest()
          .setRequestId(UUID.randomUUID().toString())
          .setParameters(args)
          .setMethodName(method.toGenericString())
          .setParameterTypes(method.getParameterTypes());
    }

    /** 获取Rpc 请求处理器 */
    private RpcClientHandler getHandled(String method) throws InterruptedException {
      ConnectManagement instance = ConnectManagement.getInstance();
      return instance.getConnectByMethod(method);
    }
  }
}
