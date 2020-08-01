package com.zhoutao123.rpc.base.annotation;

import com.zhoutao123.rpc.client.RpcClientSelector;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/** 开启RPC服务客户端,配置此注解将会扫描客户端中含有 @RpcConsumer 的服务 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcClientSelector.class)
public @interface EnableRpcClient {

  @AliasFor("scanPath")
  String value() default "";

  @AliasFor("value")
  String scanPath() default "";
}
