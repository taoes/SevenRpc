package com.zhoutao123.rpc.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcClientSelector.class)
public @interface EnableRpcClient {

  @AliasFor("scanPath")
  String value() default "";

  @AliasFor("value")
  String scanPath() default "";
}
