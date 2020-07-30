package com.zhoutao123.rpc.component.executor;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.base.annotation.RpcService;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/** 注册服务 执行器 */
@Order(2)
@Component("scanExecutor")
public class ScanExecutor implements Executor, ApplicationContextAware {

  private ApplicationContext applicationContext;

  private final RpcServiceContext rpcServiceContext;

  public ScanExecutor(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  @Override
  public void start() {
    Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(RpcService.class);

    for (Entry<String, Object> entry : rpcServiceMap.entrySet()) {
      Object serviceBean = entry.getValue();
      Class<?> aClass = serviceBean.getClass();
      RpcService rpcServiceAnnotation = aClass.getAnnotation(RpcService.class);
      Class<?>[] interfaces = aClass.getInterfaces();
      List<String> excludeMethodName = Arrays.asList(rpcServiceAnnotation.excludeMethodName());
      Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(aClass);
      for (Method method : allDeclaredMethods) {
        Method superMethod = findInterfaceMethod(interfaces, method);
        if (superMethod == null) {
          continue;
        }
        if (!excludeMethodName.contains(superMethod.getName()))
          rpcServiceContext.saveMethod(serviceBean, superMethod);
      }
    }
  }

  private Method findInterfaceMethod(Class<?>[] clazzList, Method method) {
    for (Class<?> aClass : clazzList) {
      try {
        return aClass.getMethod(method.getName(), method.getParameterTypes());
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
