package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.MethodInfo;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 注册服务 执行器 */
@Order(3)
@Component("registryExecutor")
public class RegistryExecutor implements Executor {

  private final Log log = LogFactory.get();

  private final RpcRegistry rpcRegistry;

  private final RpcServiceContext rpcServiceContext;

  public RegistryExecutor(RpcRegistry rpcRegistry, RpcServiceContext rpcServiceContext) {
    this.rpcRegistry = rpcRegistry;
    this.rpcServiceContext = rpcServiceContext;
  }

  /** 向注册中心注册服务 */
  public void start() {
    Map<String, MethodInfo> methodPool = rpcServiceContext.getMethodPool();
    log.info("Find Rpc service count:{} ", methodPool.size());
    rpcRegistry.register(methodPool.keySet());
  }
}
