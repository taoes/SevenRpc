package com.zhoutao123.rpc.component.executor.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.component.context.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 注册服务 执行器 */
@Order(3)
@Component("serviceRegistryExecutor")
public class ServiceRegistryExecutor implements Executor {

  private final Log log = LogFactory.get();

  @Autowired private RpcRegistry rpcRegistry;

  @Autowired private RpcServiceContext rpcServiceContext;

  /** 向注册中心注册服务 */
  public void start() {
    Map<String, MethodInfo> methodPool = rpcServiceContext.getMethodPool();
    log.info("Find RpcService count:{} ", methodPool.size());
    rpcRegistry.register(methodPool.keySet());
  }
}
