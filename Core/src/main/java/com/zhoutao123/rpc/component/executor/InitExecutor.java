package com.zhoutao123.rpc.component.executor;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.InitFunction;
import com.zhoutao123.rpc.base.config.RpcConfig;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** InitFunction 执行器 */
@Component("initExecutor")
public class InitExecutor implements Executor {

  private Log log = LogFactory.get();

  private RpcConfig rpcConfig;

  public InitExecutor(RpcConfig rpcConfig) {
    this.rpcConfig = rpcConfig;
  }

  /** 扫描系统中的实现 InitFunction 的方法，并初始化 */
  public void start() {
    Set<String> customPackages = rpcConfig.getCustomPackage();
    if (customPackages == null) {
      customPackages = new HashSet<>(1);
    }
    customPackages.add("com.zhoutao123.rpc");

    Set<Class<?>> classes =
        customPackages.stream()
            .map(path -> ClassUtil.scanPackageBySuper(path, InitFunction.class))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    if (classes.isEmpty()) {
      return;
    }
    try {
      List<InitBeanWrapper> wrapperList = new ArrayList<>(classes.size() + 1);
      for (Class<?> aClass : classes) {
        if (!aClass.isInterface() && InitFunction.class.isAssignableFrom(aClass)) {
          Constructor<?> constructor = aClass.getDeclaredConstructor();
          constructor.setAccessible(true);
          InitFunction initFunction = (InitFunction) constructor.newInstance();
          wrapperList.add(new InitBeanWrapper(resolverOrder(initFunction), initFunction));
        }
      }
      wrapperList.stream()
          .sorted(Comparator.comparingInt(v -> v.order))
          .map(InitBeanWrapper::getInitFunction)
          .forEach(InitFunction::init);
    } catch (Exception e) {
      log.error("初始化 InitFunction发生异常信息", e);
      System.exit(501);
    }
  }

  private int resolverOrder(InitFunction initFunction) {
    Order order = initFunction.getClass().getAnnotation(Order.class);
    if (Objects.nonNull(order)) {
      return order.value();
    }
    return 0;
  }

  static class InitBeanWrapper {

    private int order;

    private InitFunction initFunction;

    public InitBeanWrapper(int order, InitFunction initFunction) {
      this.order = order;
      this.initFunction = initFunction;
    }

    public int getOrder() {
      return order;
    }

    public void setOrder(int order) {
      this.order = order;
    }

    public InitFunction getInitFunction() {
      return initFunction;
    }

    public void setInitFunction(InitFunction initFunction) {
      this.initFunction = initFunction;
    }
  }
}
