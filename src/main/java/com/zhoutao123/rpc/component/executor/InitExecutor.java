package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.InitFunction;
import com.zhoutao123.rpc.utils.OrderUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * InitFunction 执行器
 *
 * @apiNote 扫描上下文中继承了 InitFunction 接口的实现类, 并执行 {@link InitFunction#init()} 方法
 */
@Order(1)
@Component("initExecutor")
public class InitExecutor implements Executor, ApplicationContextAware {

  private final Log log = LogFactory.get();

  private ApplicationContext applicationContext;

  @Override
  public void start() {
    Map<String, InitFunction> functionMap = applicationContext.getBeansOfType(InitFunction.class);
    if (CollectionUtils.isEmpty(functionMap)) {
      return;
    }
    try {
      List<InitBeanWrapper> wrapperList = new ArrayList<>(functionMap.size() + 1);
      for (InitFunction initFunction : functionMap.values()) {
        Integer order = OrderUtils.resolverOrder(initFunction.getClass());
        wrapperList.add(new InitBeanWrapper(order, initFunction));
      }
      wrapperList.stream()
          .sorted(Comparator.comparingInt(v -> v.order))
          .map(InitBeanWrapper::getInitFunction)
          .forEach(InitFunction::init);
    } catch (Exception e) {
      log.error("初始化 InitFunction 发生异常", e);
      System.exit(501);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Data
  @AllArgsConstructor
  static class InitBeanWrapper {

    private int order;

    private InitFunction initFunction;
  }
}
