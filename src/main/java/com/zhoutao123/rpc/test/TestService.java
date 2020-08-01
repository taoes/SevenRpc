package com.zhoutao123.rpc.test;

import java.util.Map;


public interface TestService {

  Integer sum(Integer a, Integer b);

  Map<String, Integer> result(Integer a, Integer b);

  double sqrt(int x);
}
