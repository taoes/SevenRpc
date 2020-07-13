package com.zhoutao123.rpc.component.register;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.base.config.ZkConfig;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.NodeInfo;
import com.zhoutao123.rpc.utils.NetUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

@Component
public class ZKRegister implements RpcRegistry {

  private static final String PREFIX = "/seven-rpc";

  private final Log log = LogFactory.get();

  private final RpcConfig rpcConfig;

  private final ZkClient zkClient;

  public ZKRegister(RpcConfig rpcConfig) {
    this.rpcConfig = rpcConfig;
    ZkConfig configZk = rpcConfig.getZk();
    zkClient = new ZkClient(configZk.getHost(), configZk.getPort());
  }

  @Override
  public void sendHeard() {
    zkClient.getChildren("/");
  }

  @Override
  public boolean register(Set<String> serviceNames) {
    String intranetIp = NetUtils.getIntranetIp();
    NodeInfo nodeInfo = new NodeInfo(intranetIp, rpcConfig.getPort());
    if (!zkClient.exists(PREFIX)) {
      zkClient.createEphemeral(PREFIX);
    }

    for (String serviceName : serviceNames) {
      String path = PREFIX + "/" + serviceName;
      boolean exists = zkClient.exists(path);
      List<NodeInfo> nodeInfos;
      if (exists) {
        nodeInfos = zkClient.readData(path);
        nodeInfos.add(nodeInfo);
        zkClient.delete(path);
      } else {
        nodeInfos = new ArrayList<>(1);
        nodeInfos.add(nodeInfo);
      }
      zkClient.createEphemeral(path, nodeInfos);
      log.debug("Registry service: {}", path);
    }
    return true;
  }

  @Override
  public Map<String, List<NodeInfo>> getServiceNames() {

    List<String> children = zkClient.getChildren(PREFIX);
    Map<String, List<NodeInfo>> infoMap = new HashMap<>(children.size());
    for (String node : children) {
      List<NodeInfo> nodeInfo = zkClient.readData(PREFIX + "/" + node, true);
      infoMap.put(node, nodeInfo);
    }
    return infoMap;
  }

  @Override
  public boolean unregister(Set<String> serviceNames) {
    log.info("取消注册服务完成:{}", serviceNames);
    return true;
  }
}
