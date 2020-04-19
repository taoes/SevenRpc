package com.zhoutao123.rpc.component.aop;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.annotation.RpcConsumer;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class RpcConsumerRegister implements ImportBeanDefinitionRegistrar, ApplicationContextAware {
  Log log = LogFactory.get();

  @Autowired private RpcRegistry rpcRegistry;

  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    Set<Class<?>> classes = ClassUtil.scanPackage();
    try {
      for (Class<?> aClass : classes) {
        if (aClass.getAnnotation(RpcConsumer.class) != null) {
          BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(aClass);
          GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
          definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
          definition.getPropertyValues().add("rpcRegistry", rpcRegistry);
          definition.setBeanClass(MyProxyFactory.class);
          definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

          registry.registerBeanDefinition(aClass.getName(), definition);
        }
      }
    } catch (Exception e) {
      log.error("注入bean 定义失败", e);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    NettyProxyHandler.setRpcRegistry(applicationContext);
  }

  /** SpringBean 工厂 */
  static class MyProxyFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    private RpcRegistry rpcRegistry;

    @Override
    public T getObject() throws Exception {
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

    public RpcRegistry getRpcRegistry() {
      return rpcRegistry;
    }

    public void setRpcRegistry(RpcRegistry rpcRegistry) {
      this.rpcRegistry = rpcRegistry;
    }
  }
}
