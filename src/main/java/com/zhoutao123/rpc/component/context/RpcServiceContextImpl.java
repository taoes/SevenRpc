package com.zhoutao123.rpc.component.context;

import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RpcServiceContextImpl implements RpcServiceContext {

  private Map<String, MethodInfo> methodPool;

  @Override
  public Map<String, MethodInfo> getMethodPool() {
    if (methodPool == null) {
      methodPool = new HashMap<>(10);
    }
    return methodPool;
  }

  @Override
  public MethodInfo getByName(String name) {
    if (methodPool == null) {
      return null;
    }
    return methodPool.get(name);
  }

  @Override
  public void saveMethod(Object instance, Method method) {
    if (methodPool == null) {
      methodPool = new HashMap<>(10);
    }

    String name = method.toGenericString();

    // 构建方法信息
    MethodInfo methodInfo = new MethodInfo(method, instance);
    methodPool.put(name, methodInfo);
  }
}
