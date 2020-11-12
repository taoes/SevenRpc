package com.zhoutao123.rpc.component.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.base.config.ZkConfig;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.NodeInfo;
import com.zhoutao123.rpc.utils.NetUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/** 服务注册器 */
@Component
public class ServiceRegister implements RpcRegistry {

  private static final String PREFIX = "/SEVEN_RPC";

  private final Log log = LogFactory.get();

  private final NodeInfo localNode;

  private final ZkClient zkClient;

  public ServiceRegister(RpcConfig rpcConfig) {
    ZkConfig configZk = rpcConfig.getZk();
    zkClient = new ZkClient(configZk.getHost(), configZk.getPort());
    if (!zkClient.exists(PREFIX)) {
      zkClient.createPersistent(PREFIX);
    }
    String localIP = NetUtils.getIntranetIp();
    localNode = new NodeInfo(localIP, rpcConfig.getPort());
  }

  @Override
  public void sendHeard() {}

  @Override
  public void register(Set<String> serviceNames) {

    for (String serviceName : serviceNames) {
      String path = PREFIX + "/" + serviceName;
      boolean exists = zkClient.exists(path);
      Set<NodeInfo> nodeInfos;

      if (exists) {
        nodeInfos = zkClient.readData(path);
        nodeInfos.add(localNode);
        zkClient.delete(path);
      } else {
        nodeInfos = new HashSet<>();
        nodeInfos.add(localNode);
      }
      zkClient.createEphemeral(path, nodeInfos);
      log.debug("Registry service: {}", path);
    }
  }

  @Override
  public Map<String, Set<NodeInfo>> getServiceNames() {
    List<String> children = zkClient.getChildren(PREFIX);
    Map<String, Set<NodeInfo>> infoMap = new HashMap<>(children.size());
    for (String node : children) {
      Set<NodeInfo> nodeInfo = zkClient.readData(PREFIX + "/" + node, true);
      infoMap.put(node, nodeInfo);
    }
    return infoMap;
  }

  @Override
  public void unregister(Set<String> serviceNames) {
    if (CollectionUtils.isEmpty(serviceNames)) {
      return;
    }
    for (String serviceName : serviceNames) {
      String path = PREFIX + "/" + serviceName;
      boolean exist = zkClient.exists(path);
      if (!exist) {
        continue;
      }
      List<NodeInfo> nodeList = zkClient.readData(path);
      nodeList.remove(localNode);
    }

    log.info("取消注册服务完成:{}", serviceNames);
  }
}
