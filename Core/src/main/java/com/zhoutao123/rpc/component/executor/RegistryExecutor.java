package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.MethodInfo;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 注册服务 执行器 */
@Component("registryExecutor")
public class RegistryExecutor implements Executor {

  private Log log = LogFactory.get();

  private RpcRegistry rpcRegistry;

  private RpcServiceContext rpcServiceContext;

  public RegistryExecutor(RpcRegistry rpcRegistry, RpcServiceContext rpcServiceContext) {
    this.rpcRegistry = rpcRegistry;
    this.rpcServiceContext = rpcServiceContext;
  }

  /** 向注册中心注册服务 */
  public void start() {
    Map<String, MethodInfo> methodPoll = rpcServiceContext.getMethodPool();
    rpcRegistry.register(methodPoll.keySet());
  }
}
