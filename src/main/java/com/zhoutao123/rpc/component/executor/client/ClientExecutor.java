package com.zhoutao123.rpc.component.executor.client;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 初始化客户端连接器 */
@Order
@Component("clientExecutor")
public class ClientExecutor implements Executor, ApplicationRunner {

  @Autowired @Lazy private RpcRegistry rpcRegistry;

  /** 从注册中心获取连接信息 */
  @Override
  public void start() {}

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Map<String, Set<NodeInfo>> serviceNames = rpcRegistry.getServiceNames();

    ConnectManagement instance = ConnectManagement.getInstance();
    for (Entry<String, Set<NodeInfo>> serviceEntry : serviceNames.entrySet()) {
      String method = serviceEntry.getKey();
      Set<NodeInfo> nodeSet = serviceEntry.getValue();
      for (NodeInfo nodeInfo : nodeSet) {
        instance.connectServerNode(method, nodeInfo);
      }
    }
  }
}
