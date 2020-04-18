package com.zhoutao123.rpc.demo;

import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TestController {

  private RpcServiceContext serviceContext;

  public TestController(RpcServiceContext serviceContext) {
    this.serviceContext = serviceContext;
  }

  @GetMapping("/method")
  public List<String> test() {
    Map<String, MethodInfo> methodPool = serviceContext.getMethodPool();
    List<String> methodNames =
        methodPool.values().stream().map(MethodInfo::getMethodName).collect(Collectors.toList());
    return methodNames;
  }
}
