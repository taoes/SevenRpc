package com.zhoutao123.rpc.component.executor.client;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.component.client.SocketAddressWrapper;
import com.zhoutao123.rpc.entity.NodeInfo;
import com.zhoutao123.rpc.utils.NetUtils;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 注册节点服务 */
@Order
@Component("clientExecutor")
public class ClientExecutor implements Executor {

  @Autowired private RpcRegistry rpcRegistry;

  @Autowired private RpcConfig rpcConfig;

  /** 从注册中心获取连接信息 */
  @Override
  public void start() {
    Map<String, Set<NodeInfo>> serviceNames = rpcRegistry.getServiceNames();
    Collection<Set<NodeInfo>> nodeInfos = serviceNames.values();

    // 构建本机断点
    Integer port = rpcConfig.getPort();
    String intranetIp = NetUtils.getIntranetIp();

    NodeInfo localNodeInfo = new NodeInfo(intranetIp, port);

    List<InetSocketAddress> addresses =
        nodeInfos.stream()
            .flatMap(Collection::stream)
            .filter(nodeInfo -> !Objects.equals(nodeInfo, localNodeInfo))
            .map(NodeInfo::toAddress)
            .collect(Collectors.toList());

    ConnectManagement instance = ConnectManagement.getInstance();
    try {
      for (InetSocketAddress address : addresses) {
        instance.connectServerNode(SocketAddressWrapper.getInstance(address));
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
