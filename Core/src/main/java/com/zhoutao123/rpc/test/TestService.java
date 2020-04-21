package com.zhoutao123.rpc.test;

import com.zhoutao123.rpc.base.annotation.RpcConsumer;
import com.zhoutao123.rpc.base.annotation.RpcMethod;
import java.util.Map;

@RpcConsumer
public interface TestService {

  /** 发送请求 */
  @RpcMethod
  Integer sum(Integer a, Integer b);

  @RpcMethod
  Map<String, Integer> result(Integer a, Integer b);
}
