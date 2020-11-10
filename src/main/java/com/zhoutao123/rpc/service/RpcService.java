package com.zhoutao123.rpc.service;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.component.executor.client.ClientExecutor;
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

/** 启动SevenRPC Service 客户端 */
@Slf4j
public class RpcService implements ApplicationRunner, ApplicationContextAware {

  private ApplicationContext context;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    final long startTime = System.currentTimeMillis();
    // 打印logo
    LogoUtils.printLogo();

    // 运行定义的执行器
    runExecutor();

    log.info("Start RpcService in {}  ms!", System.currentTimeMillis() - startTime);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }

  private void runExecutor() {
    Map<String, Executor> beansOfType = this.context.getBeansOfType(Executor.class);
    List<Executor> collect =
        beansOfType.values().stream()
            .sorted(
                (execute1, execute2) -> {
                  Order order1 = execute1.getClass().getAnnotation(Order.class);
                  Order order2 = execute2.getClass().getAnnotation(Order.class);
                  if (order1 == null || order2 == null) {
                    return 0;
                  }
                  return order1.value() - order2.value();
                })
            .filter(executor -> !(executor instanceof ClientExecutor))
            .collect(Collectors.toList());
    // 执行器
    for (Executor executor : collect) {
      executor.start();
    }
    log.debug("SevenRPC run executor finished!");
  }
}
