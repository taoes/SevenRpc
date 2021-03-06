package com.zhoutao123.rpc.service;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.component.executor.ClientExecutor;
import com.zhoutao123.rpc.utils.LogoUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

@Slf4j
public class RpcService implements ApplicationRunner, ApplicationContextAware {

  // Spring应用上下文
  private ApplicationContext context;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    long startTime = System.currentTimeMillis();
    Map<String, Executor> beansOfType = this.context.getBeansOfType(Executor.class);

    // 对执行器排序
    List<Executor> collect =
        beansOfType.values().stream()
            .sorted(
                (v1, v2) -> {
                  Order order1 = v1.getClass().getAnnotation(Order.class);
                  Order order2 = v2.getClass().getAnnotation(Order.class);
                  if (order1 == null || order2 == null) {
                    return 0;
                  }
                  return order1.value() - order2.value();
                })
            .filter(executor -> !(executor instanceof ClientExecutor))
            .collect(Collectors.toList());
    LogoUtils.printLogo();
    for (Executor executor : collect) {
      executor.start();
    }
    log.info("Start RpcService in {}  ms!", System.currentTimeMillis() - startTime);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }
}
