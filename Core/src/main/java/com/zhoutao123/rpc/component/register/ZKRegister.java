package com.zhoutao123.rpc.component.register;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.base.config.ZkConfig;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.NodeInfo;
import com.zhoutao123.rpc.utils.NetUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RpcConfig.class)
public class ZKRegister implements RpcRegistry {

  private static final String PREFIX = "/seven-rpc";

  private Log log = LogFactory.get();

  private RpcConfig rpcConfig;

  private ZkClient zkClient;

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

    serviceNames.forEach(
        serviceName -> {
          String path = PREFIX + "/" + serviceName;
          zkClient.delete(path);
          zkClient.createEphemeral(path, nodeInfo);
        });
    return true;
  }

  @Override
  public Map<String, NodeInfo> getServiceNames() {

    List<String> children = zkClient.getChildren(PREFIX);
    Map<String, NodeInfo> infoMap = new HashMap<>(children.size());
    for (String node : children) {
      NodeInfo nodeInfo = zkClient.<NodeInfo>readData(PREFIX + "/" + node, true);
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
