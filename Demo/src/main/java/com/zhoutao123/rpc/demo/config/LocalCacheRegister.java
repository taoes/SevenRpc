package com.zhoutao123.rpc.demo.config;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.registry.RpcRegistry;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LocalCacheRegister implements RpcRegistry {

  Log log = LogFactory.get();

  @Override
  public void sendHeard() {
    log.info("发送心跳包");
  }

  @Override
  public boolean register(Set<String> serviceNames) {
    log.info("注册服务完成");
    serviceNames.forEach(info -> log.warn("- {}", info));
    return true;
  }

  @Override
  public boolean unregister(Set<String> serviceNames) {
    log.info("取消注册服务完成:{}", serviceNames);
    return true;
  }
}
