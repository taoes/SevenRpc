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

  /**
   * 获取所有的方法信息
   *
   * @return 返回方法信息
   */
  Map<String, MethodInfo> getMethodPool();

  /**
   * 获取方法信息
   *
   * @param name 方法名称
   * @return 返回对应的方法信息
   */
  MethodInfo getByName(String name);

  /**
   * 保存方法到上下文中
   *
   * @param instance 执行实例
   * @param method 执行的方法
   */
  void saveMethod(Object instance, Method method);
}
