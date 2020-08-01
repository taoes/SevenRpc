package com.zhoutao123.rpc.service.impl;

import com.zhoutao123.rpc.base.annotation.RpcService;
import com.zhoutao123.rpc.test.TestService;
import java.util.HashMap;
import java.util.Map;

@RpcService
public class RpcServiceTest implements TestService {

  @Override
  public Integer sum(Integer a, Integer b) {
    return a + b;
  }

  @Override
  public Map<String, Integer> result(Integer a, Integer b) {
    Map<String, Integer> map = new HashMap<>(3);
    map.put("和", a + b);
    map.put("差", a - b);
    map.put("积", a * b);
    return map;
  }

  @Override
  public double sqrt(int x) {
    return Math.sqrt(x);
  }
}
