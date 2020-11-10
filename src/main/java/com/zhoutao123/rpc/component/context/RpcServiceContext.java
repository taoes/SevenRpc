package com.zhoutao123.rpc.component.context;

import com.zhoutao123.rpc.entity.MethodInfo;
import com.zhoutao123.rpc.utils.HashUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RpcServiceContext {

  private Map<String, MethodInfo> methodPool;

  public Map<String, MethodInfo> getMethodPool() {
    if (methodPool == null) {
      methodPool = new HashMap<>(10);
    }
    return methodPool;
  }

  public void saveMethod(Object instance, Method method) {
    if (methodPool == null) {
      methodPool = new HashMap<>();
    }

    String name = HashUtils.md5(method.toGenericString());
    MethodInfo methodInfo = new MethodInfo(method, instance);
    methodPool.putIfAbsent(name, methodInfo);
  }
}
