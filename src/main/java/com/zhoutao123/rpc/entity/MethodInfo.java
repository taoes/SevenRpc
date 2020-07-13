package com.zhoutao123.rpc.entity;

import java.lang.reflect.Method;
import lombok.Data;

/** 方法信息实体 */
@Data
public class MethodInfo {

  private final Method method;

  private final Object instance;

  public MethodInfo(Method method, Object instance) {

    this.method = method;
    this.instance = instance;
  }

  public Method getMethod() {
    return method;
  }

  public Object getInstance() {
    return instance;
  }

  public String getMethodName() {
    return method.toGenericString();
  }
}
