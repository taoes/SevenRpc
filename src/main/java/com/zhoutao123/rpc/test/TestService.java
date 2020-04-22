package com.zhoutao123.rpc.test;

import com.zhoutao123.rpc.base.annotation.RpcConsumer;
import java.util.Map;

@RpcConsumer
public interface TestService {

  Integer sum(Integer a, Integer b);

  Map<String, Integer> result(Integer a, Integer b);

  double sqrt(int x);
}
