package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.base.config.ZkConfig;
import com.zhoutao123.rpc.base.constant.ZkValue;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.component.listener.ZKDataListener;
import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

/** 服务注册器 */
@Slf4j
@Component
public class RpcClientRegister implements RpcRegistry {

  private final ZkClient zkClient;

  public RpcClientRegister(RpcConfig rpcConfig) {
    ZkConfig configZk = rpcConfig.getZk();
    zkClient = new ZkClient(configZk.getHost(), 1000, 1000);
    if (!zkClient.exists(ZkValue.PREFIX)) {
      zkClient.createPersistent(ZkValue.PREFIX);
    }
  }

  @Override
  public void sendHeard() {}

  @Override
  public void register(Set<String> serviceNames) {}

  @Override
  public Map<String, Set<NodeInfo>> getServiceNames() {
    List<String> childrenList = zkClient.getChildren(ZkValue.PREFIX);
    Map<String, Set<NodeInfo>> infoMap = new HashMap<>(childrenList.size());
    for (String children : childrenList) {
      String childrenPath = ZkValue.PREFIX_SLASH+ children;
      zkClient.subscribeDataChanges(childrenPath, new ZKDataListener());
      Set<NodeInfo> nodeInfo = zkClient.readData(childrenPath, true);
      infoMap.put(children, nodeInfo);
    }
    return infoMap;
  }

  @Override
  public void unregister(Set<String> serviceNames) {}
}
