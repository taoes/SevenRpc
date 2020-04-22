package com.zhoutao123.rpc.base.annotation;

import com.zhoutao123.rpc.component.aop.RpcConsumerRegister;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/** 启用RPC消费者服务 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RpcConsumerRegister.class)
public @interface EnabledRpcConsumer {

  String[] scanPath() default {};
}
