package com.zhoutao123.rpc.base.annotation;

import com.zhoutao123.rpc.service.RpcServiceSelector;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/** 启用RPC服务 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcServiceSelector.class)
public @interface EnableRpcService {}
