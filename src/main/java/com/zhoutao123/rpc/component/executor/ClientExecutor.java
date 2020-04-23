package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.entity.NodeInfo;
import com.zhoutao123.rpc.utils.NetUtils;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 注册服务 执行器 */
@Component("clientExecutor")
public class ClientExecutor implements Executor {

  private Log log = LogFactory.get();

  private RpcRegistry rpcRegistry;

  private RpcConfig rpcConfig;

  public ClientExecutor(RpcRegistry rpcRegistry, RpcConfig rpcConfig) {
    this.rpcRegistry = rpcRegistry;
    this.rpcConfig = rpcConfig;
  }

  /** 从注册中心获取连接信息 */
  public void start() {
    Map<String, NodeInfo> serviceNames = rpcRegistry.getServiceNames();
    HashSet<NodeInfo> nodeInfos = new HashSet<>(serviceNames.values());

    // 构建本机断点
    Integer port = rpcConfig.getPort();
    String intranetIp = NetUtils.getIntranetIp();

    NodeInfo localNodeInfo = new NodeInfo(intranetIp, port);

    List<InetSocketAddress> addresses =
        nodeInfos.stream()
            .filter(nodeInfo -> !Objects.equals(nodeInfo, localNodeInfo))
            .map(NodeInfo::toAddress)
            .collect(Collectors.toList());

    ConnectManagement instance = ConnectManagement.getInstance();
    instance.connectServerNode(addresses);
  }
}
