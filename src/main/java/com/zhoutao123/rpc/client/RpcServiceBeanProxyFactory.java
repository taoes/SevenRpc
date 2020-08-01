package com.zhoutao123.rpc.client;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

/** RPC 服务Bean生成代理对象 */
@Data
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
}
