package com.zhoutao123.rpc.service;

import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.component.context.RpcServiceContextImpl;
import com.zhoutao123.rpc.component.executor.ClientExecutor;
import com.zhoutao123.rpc.component.executor.InitExecutor;
import com.zhoutao123.rpc.component.executor.NettyExecutor;
import com.zhoutao123.rpc.component.executor.RegistryExecutor;
import com.zhoutao123.rpc.component.executor.ScanExecutor;
import com.zhoutao123.rpc.component.register.ZKRegister;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class RpcServiceConfiguration implements ImportSelector {

  public String[] selectImports(AnnotationMetadata importingClassMetadata) {

    return new String[] {
      RpcService.class.getName(),
      RpcServiceContextImpl.class.getName(),
      ZKRegister.class.getName(),
      RpcConfig.class.getName(),
      ClientExecutor.class.getName(),
      InitExecutor.class.getName(),
      NettyExecutor.class.getName(),
      RegistryExecutor.class.getName(),
      ScanExecutor.class.getName(),
    };
  }
}
