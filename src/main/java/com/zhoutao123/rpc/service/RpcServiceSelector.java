package com.zhoutao123.rpc.service;

import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.component.context.RpcServiceContext;
import com.zhoutao123.rpc.component.executor.InitExecutor;
import com.zhoutao123.rpc.component.executor.service.ServiceNettyExecutor;
import com.zhoutao123.rpc.component.executor.service.ServiceRegistryExecutor;
import com.zhoutao123.rpc.component.executor.service.ServiceScanExecutor;
import com.zhoutao123.rpc.component.service.ServiceRegister;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class RpcServiceSelector implements ImportSelector {

  /** 服务端需要注入的Bean 实例 */
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {

    return new String[] {
      // Rpc 服务启动入口
      RpcService.class.getName(),

      // 配置信息
      RpcConfig.class.getName(),

      // Rpc 服务端上下文
      RpcServiceContext.class.getName(),

      // ZK 客户端
      ServiceRegister.class.getName(),

      // 注入相关执行器
      InitExecutor.class.getName(),
      ServiceScanExecutor.class.getName(),
      ServiceRegistryExecutor.class.getName(),
      ServiceNettyExecutor.class.getName(),
    };
  }
}
