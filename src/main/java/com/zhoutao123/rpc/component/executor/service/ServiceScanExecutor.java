package com.zhoutao123.rpc.component.executor.service;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.annotation.service.RpcService;
import com.zhoutao123.rpc.component.context.RpcServiceContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 扫描系统中使用了注解 {@link RpcService }的服务类
 *
 * <p>并提取信息保存到Rpc服务上下文 {@link RpcServiceContext#saveMethod(Object,Method)}</>
 */
@Order(2)
@Component("serviceScanExecutor")
public class ServiceScanExecutor implements Executor, ApplicationContextAware {

  private ApplicationContext applicationContext;

  private final RpcServiceContext rpcServiceContext;

  public ServiceScanExecutor(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  @Override
  public void start() {
    Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(RpcService.class);

    for (Object serviceBean : rpcServiceMap.values()) {
      Class<?> serviceBeanClass = serviceBean.getClass();
      RpcService rpcServiceAnnotation = serviceBeanClass.getAnnotation(RpcService.class);
      if (rpcServiceAnnotation == null) {
        continue;
      }

      registryRpcServiceMethod(serviceBean, rpcServiceAnnotation);
    }
  }

  /** 注册RPC服务函数 */
  private void registryRpcServiceMethod(Object serviceBean, RpcService rpcServiceAnnotation) {
    Class<?> serviceBeanClass = serviceBean.getClass();
    Class<?>[] interfaces = serviceBeanClass.getInterfaces();

    // 排除的方法名称
    List<String> excludeMethodName = Arrays.asList(rpcServiceAnnotation.excludeMethodName());

    Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(serviceBeanClass);
    for (Method method : allDeclaredMethods) {
      Method methodFromInterface = findInterfaceMethod(interfaces, method);
      if (methodFromInterface == null) {
        continue;
      }
      if (rpcServiceAnnotation.all()) {
        rpcServiceContext.saveMethod(serviceBean, methodFromInterface);
      } else if (!excludeMethodName.contains(methodFromInterface.getName())) {
        rpcServiceContext.saveMethod(serviceBean, methodFromInterface);
      }
    }
  }

  /** 通过方法名和参数类型找到接口中的方法 */
  private Method findInterfaceMethod(Class<?>[] clazzList, Method method) {
    String methodName = method.getName();
    Class<?>[] paramTypes = method.getParameterTypes();
    for (Class<?> aClass : clazzList) {
      try {
        return aClass.getMethod(methodName, paramTypes);
      } catch (Exception ignored) {

      }
    }
    return null;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
