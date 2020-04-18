package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.base.annotation.RpcMethod;
import com.zhoutao123.rpc.base.annotation.RpcService;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/** 注册服务 执行器 */
@Component("scanExecutor")
public class ScanExecutor implements Executor {

  private Log log = LogFactory.get();

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
      Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(serviceBean.getClass());
      for (Method method : allDeclaredMethods) {
        RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
        if (rpcMethod == null) {
          continue;
        }
        rpcServiceContext.saveMethod(serviceBean, method);
      }
    }
  }
}
