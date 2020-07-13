package com.zhoutao123.rpc.component.executor;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.base.annotation.RpcService;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/** 注册服务 执行器 */
@Order(2)
@Component("scanExecutor")
public class ScanExecutor implements Executor {

  private ApplicationContext applicationContext;

  private RpcServiceContext rpcServiceContext;

  public ScanExecutor(ApplicationContext applicationContext, RpcServiceContext rpcServiceContext) {
    this.applicationContext = applicationContext;
    this.rpcServiceContext = rpcServiceContext;
  }

  /** 开始扫描系统的服务，向注册中心注册服务 */
  public void start() {
    Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(RpcService.class);

    for (Entry<String, Object> entry : rpcServiceMap.entrySet()) {
      Object serviceBean = entry.getValue();
      Class<?>[] interfaces = serviceBean.getClass().getInterfaces();

      Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(serviceBean.getClass());
      for (Method method : allDeclaredMethods) {
        Method superMethod = findInterfaceMethod(interfaces, method);
        if (superMethod == null) {
          continue;
        }
        rpcServiceContext.saveMethod(serviceBean, superMethod);
      }
    }
  }

  private Method findInterfaceMethod(Class<?>[] clazzList, Method method) {
    for (Class<?> aClass : clazzList) {
      try {
        Method method1 = aClass.getMethod(method.getName(), method.getParameterTypes());
        if (method1 != null) {
          return method1;
        }
      } catch (Exception ignored) {

      }
    }

    return null;
  }
}
