package com.zhoutao123.rpc.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.annotation.EnabledRpcConsumer;
import com.zhoutao123.rpc.component.executor.ClientExecutor;
import com.zhoutao123.rpc.component.executor.InitExecutor;
import com.zhoutao123.rpc.component.executor.NettyExecutor;
import com.zhoutao123.rpc.component.executor.RegistryExecutor;
import com.zhoutao123.rpc.component.executor.ScanExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

public class RpcService implements ApplicationRunner, ApplicationContextAware {

  private static final Log log = LogFactory.get();

  private ApplicationContext context;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Map<String, Executor> beansOfType = this.context.getBeansOfType(Executor.class);
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

    long startTime = System.currentTimeMillis();
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
