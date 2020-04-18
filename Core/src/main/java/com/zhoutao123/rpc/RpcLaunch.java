package com.zhoutao123.rpc;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.component.InitExecutor;
import com.zhoutao123.rpc.component.NettyExecutor;
import com.zhoutao123.rpc.component.RegistryExecutor;
import com.zhoutao123.rpc.component.ScanExecutor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RpcLaunch {

  private static Log log = LogFactory.get();

  private List<Executor> executors;

  public RpcLaunch(
      RegistryExecutor registryExecutor,
      NettyExecutor nettyExecutor,
      InitExecutor initExecutor,
      ScanExecutor scanExecutor) {
    executors = new ArrayList<>(4);
    // 初始化
    this.executors.add(initExecutor);
    // 扫描
    this.executors.add(scanExecutor);
    // 注册
    this.executors.add(registryExecutor);
    // 初始化Netty
    this.executors.add(nettyExecutor);
  }

  public void start() {
    log.info("SevenRpc is starting....");
    this.executors.forEach(Executor::start);
    log.info("SevenRpc start at port: {}", RpcConfig.SERVICE_PORT);
  }
}
