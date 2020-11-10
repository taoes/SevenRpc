package com.zhoutao123.rpc.base.annotation.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;

/** 标记此服务转为RPC服务 */
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

  String[] excludeMethodName() default {};

  /**
   * 注册 RPC服务类中所有的方法
   *
   * @apiNote 不包含 {@link Object} 类中的方法
   */
  boolean all() default false;
}
