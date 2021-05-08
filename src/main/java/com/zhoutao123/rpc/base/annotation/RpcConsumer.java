package com.zhoutao123.rpc.base.annotation;

import com.zhoutao123.rpc.base.config.RpcConfig.DefaultConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 用于声明服务使用Seven RPC 注入 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcConsumer {

  /** 启用直连模式 */
  boolean enableDirect() default false;

  /** 直连模式的主机地址 */
  String host() default "localhost";

  /** 直连模式的端口地址 */
  int port() default DefaultConfig.port;
}
