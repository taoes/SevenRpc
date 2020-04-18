package com.zhoutao123.rpc.base.registry;

import java.util.Set;

/** RPC 注册中心接口 */
public interface RpcRegistry {

  /** 发送心跳 */
  void sendHeard();

  /** 批量注册服务 */
  boolean register(Set<String> serviceNames);

  /** 取消注册服务 */
  boolean unregister(Set<String> serviceNames);
}
