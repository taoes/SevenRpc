package com.zhoutao123.rpc.demo;

import com.zhoutao123.rpc.base.annotation.RpcMethod;
import com.zhoutao123.rpc.base.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserService {

  @RpcMethod
  public String test() {
    return "SUCCESS";
  }

  @RpcMethod
  public String test2(String name, String age) {
    return name + "@" + age;
  }
}
