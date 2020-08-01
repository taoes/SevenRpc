package com.zhoutao123.rpc.client;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.annotation.EnableRpcClient;
import com.zhoutao123.rpc.base.annotation.RpcConsumer;
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
import org.springframework.util.StringUtils;

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

    // 获取属性注解中的ScanPath 属性, 用户扫描路径中带有@RpcConsumer 的服务
    String path = (String) attributes.get("scanPath");
    if (!StringUtils.hasText(path)) {
      log.warn("rpc scan path not set!");
      return;
    }

    Set<Class<?>> classes = ClassUtil.scanPackage(path);
    for (Class<?> aClass : classes) {
      // 获取目标类的私有属性
      Field[] fields = aClass.getDeclaredFields();
      for (Field field : fields) {
        // 判断目标字段是否含有 RpcConsumer 注解，如果有的话，则向IOC容器中注入
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
    boolean addComplete = classes.add(clazz);
    if (!addComplete) {
      return;
    }
    String beanName = clazz.getName();
    // 如果此Bean已经在IOC容器中注入，那么不在进行二次注入
    boolean containsBeanDefinition = registry.containsBeanDefinition(beanName);
    if (containsBeanDefinition) {
      return;
    }

    // 获取代理BeanClass
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);

    GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
    definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
    definition.setBeanClass(RpcServiceBeanProxyFactory.class);
    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
    registry.registerBeanDefinition(clazz.getName(), definition);
  }
}
