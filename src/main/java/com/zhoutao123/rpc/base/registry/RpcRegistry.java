package com.zhoutao123.rpc.base.registry;

import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.Map;
import java.util.Set;

/** RPC 注册中心接口 */
public interface RpcRegistry {

  /** 发送心跳 */
  void sendHeard();

  /**
   * 批量注册服务
   *
   * @param serviceNames 服务的名称
   * @return 是否注册成功
   */
  boolean register(Set<String> serviceNames);

  /**
   * 获取节点的服务列表
   *
   * @return 提供的方法信息
   */
  Map<String, NodeInfo> getServiceNames();

  /**
   * 取消注册服务
   *
   * @param serviceNames 注销的方法名称
   * @return 是否注销成功
   */
  boolean unregister(Set<String> serviceNames);
}
