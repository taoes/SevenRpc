package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.component.aop.NettyProxyHandler;
import org.springframework.beans.factory.FactoryBean;

public class MyProxyFactory<T> implements FactoryBean<T> {

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

  public Class<T> getInterfaceClass() {
    return interfaceClass;
  }

  public void setInterfaceClass(Class<T> interfaceClass) {
    this.interfaceClass = interfaceClass;
  }
}
