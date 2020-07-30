package com.zhoutao123.rpc.component.aop;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.annotation.EnabledRpcConsumer;
import com.zhoutao123.rpc.base.annotation.RpcConsumer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class RpcConsumerRegister implements ImportBeanDefinitionRegistrar {

  private final Log log = LogFactory.get();

  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

    Set<String> scanPathList = resolveScanPath(metadata);
    Set<Class<?>> classes;

    if (scanPathList.size() > 0) {
      classes =
          scanPathList.stream()
              .map(ClassUtil::scanPackage)
              .flatMap(Set::stream)
              .collect(Collectors.toSet());
    } else {
      classes = ClassUtil.scanPackage();
    }

    try {
      for (Class<?> aClass : classes) {
        if (aClass.isInterface() && aClass.getAnnotation(RpcConsumer.class) != null) {
          BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(aClass);
          GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
          definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
          definition.setBeanClass(MyProxyFactory.class);
          definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
          registry.registerBeanDefinition(aClass.getName(), definition);
        }
      }
    } catch (Exception e) {
      log.error("Autowired RpcBean definition fail", e);
    }
  }

  /** 从注解信息中获取配置信息 */
  private Set<String> resolveScanPath(AnnotationMetadata metadata) {
    Map<String, Object> attributes =
        metadata.getAnnotationAttributes(EnabledRpcConsumer.class.getName());
    if (attributes == null) {
      return new HashSet<>(0);
    }
    String[] paths = (String[]) attributes.get("scanPath");
    return new HashSet<>(Arrays.asList(paths));
  }

  /** SpringBean 工厂 */
  static class MyProxyFactory<T> implements FactoryBean<T> {

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
}
