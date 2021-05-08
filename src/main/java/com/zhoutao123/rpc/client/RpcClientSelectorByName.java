package com.zhoutao123.rpc.client;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.component.executor.client.ClientExecutor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class RpcClientSelectorByName implements ImportSelector {

  public static final Log log = LogFactory.get();

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return new String[] {
      RpcConfig.class.getName(), ClientExecutor.class.getName(), RpcClientRegister.class.getName()
    };
  }
}
