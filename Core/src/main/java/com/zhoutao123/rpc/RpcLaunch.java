package com.zhoutao123.rpc;

import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.component.executor.ClientExecutor;
import com.zhoutao123.rpc.component.executor.InitExecutor;
import com.zhoutao123.rpc.component.executor.NettyExecutor;
import com.zhoutao123.rpc.component.executor.RegistryExecutor;
import com.zhoutao123.rpc.component.executor.ScanExecutor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RpcLaunch implements ApplicationRunner {

  private List<Executor> executors;

  public RpcLaunch(
      RegistryExecutor registryExecutor,
      NettyExecutor nettyExecutor,
      InitExecutor initExecutor,
      ClientExecutor clientExecutor,
      ScanExecutor scanExecutor) {
    executors = new ArrayList<>(4);
    this.executors.add(initExecutor);
    this.executors.add(scanExecutor);
    this.executors.add(registryExecutor);
    this.executors.add(clientExecutor);
    this.executors.add(nettyExecutor);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    this.executors.forEach(Executor::start);
  }
}
