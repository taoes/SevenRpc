package com.zhoutao123.rpc.component.aop;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.context.ApplicationContext;

/** 定义的动态代理对象 */
public class NettyProxyHandler implements InvocationHandler {

  Log log = LogFactory.get();

  private static ApplicationContext context;

  private static RpcRegistry rpcRegistry;

  public static void setRpcRegistry(ApplicationContext context) {
    NettyProxyHandler.context = context;
  }

  public Object bind(Class<?> cls) {
    return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    synchronized (this) {
      if (rpcRegistry == null) {
        synchronized (this) {
          rpcRegistry = context.getBean(RpcRegistry.class);
        }
      }
    }

    log.info("执行代理方法:{}", method.toGenericString());
    return null;
  }
}
