package com.zhoutao123.rpc.base;

import com.zhoutao123.rpc.entity.MethodInfo;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC服务的上下文
 *
 * @author zhoutao
 * @since 0.0.1
 */
public interface RpcServiceContext {

  /** 获取所有的方法信息 */
  Map<String, MethodInfo> getMethodPool();

  /** 获取方法信息 */
  MethodInfo getByName(String name);

  /**
   * 保存方法到上下文中
   *
   * @param instance 执行该方法的实例对象
   * @param method 该方法
   */
  void saveMethod(Object instance, Method method);
}
