package com.zhoutao123.rpc.demo;

import com.zhoutao123.rpc.RpcLaunch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.zhoutao123"})
public class DemoApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
    RpcLaunch bean = context.getBean(RpcLaunch.class);
    bean.start();
  }
}
