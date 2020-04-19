package com.zhoutao123.rpc.base.registry;

import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.Map;
import java.util.Set;

/** RPC 注册中心接口 */
public interface RpcRegistry {

  /** 发送心跳 */
  void sendHeard();

  /** 批量注册服务 */
  boolean register(Set<String> serviceNames);

  /** 获取节点的服务列表
   * @return*/
  Map<String, NodeInfo> getServiceNames();

  /** 取消注册服务 */
  boolean unregister(Set<String> serviceNames);
}
