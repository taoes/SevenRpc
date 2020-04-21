package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.entity.NodeInfo;
import com.zhoutao123.rpc.utils.NetUtils;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 注册服务 执行器 */
@Component("clientExecutor")
public class ClientExecutor implements Executor {

  private Log log = LogFactory.get();

  private RpcRegistry rpcRegistry;

  public ClientExecutor(RpcRegistry rpcRegistry) {
    this.rpcRegistry = rpcRegistry;
  }

  /** 从注册中心获取连接信息 */
  public void start() {
    Map<String, NodeInfo> serviceNames = rpcRegistry.getServiceNames();
    HashSet<NodeInfo> nodeInfos = new HashSet<>(serviceNames.values());

    ConnectManagement instance = ConnectManagement.getInstance();
    String intranetIp = NetUtils.getIntranetIp();

    List<InetSocketAddress> addresses =
        nodeInfos.stream().map(NodeInfo::toAddress).collect(Collectors.toList());
    instance.connectServerNode(addresses);
  }
}
