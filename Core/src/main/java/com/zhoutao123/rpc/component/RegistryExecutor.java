package com.zhoutao123.rpc.component;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import java.util.Map;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

/** 注册服务 执行器 */
@Component("registryExecutor")
public class RegistryExecutor implements Executor {

  private Log log = LogFactory.get();

  private ZooKeeper zooKeeper;

  private RpcServiceContext rpcServiceContext;

  public RegistryExecutor(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  /** 开始扫描系统的服务，向注册中心注册服务 */
  public void start() {
    Map<String, MethodInfo> methodPoll = rpcServiceContext.getMethodPool();
  }
}
