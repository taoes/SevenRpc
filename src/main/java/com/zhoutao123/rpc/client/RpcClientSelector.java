package com.zhoutao123.rpc.client;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

@Configuration
public class RpcClientSelector implements ImportBeanDefinitionRegistrar {

  public static final Log log = LogFactory.get();

  private final Set<Class<?>> classes = new HashSet<>();

  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    Map<String, Object> attributes =
        importingClassMetadata.getAnnotationAttributes(EnableRpcClient.class.getName());
    if (CollectionUtils.isEmpty(attributes)) {
      return;
    }
    String path = (String) attributes.get("path");
    Set<Class<?>> classes = ClassUtil.scanPackage(path);
    for (Class<?> aClass : classes) {
      Field[] fields = aClass.getDeclaredFields();
      for (Field field : fields) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
          Class<? extends Annotation> annotationType = annotation.annotationType();
          boolean assignableFrom = annotationType.isAssignableFrom(RpcConsumer.class);
          if (assignableFrom) {
            registryRpcServiceBean(registry, field.getType());
          }
        }
      }
    }
  }

  private void registryRpcServiceBean(BeanDefinitionRegistry registry, Class<?> clazz) {
    boolean add = classes.add(clazz);
    if (!add) {
      return;
    }
    String name = clazz.getName();
    boolean containsBeanDefinition = registry.containsBeanDefinition(name);
    if (containsBeanDefinition) {
      return;
    }

    // 获取代理BeanClass

    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
    GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
    definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
    definition.setBeanClass(MyProxyFactory.class);
    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
    registry.registerBeanDefinition(clazz.getName(), definition);

    log.info("注入RpcBean:{}", name);
  }
}

