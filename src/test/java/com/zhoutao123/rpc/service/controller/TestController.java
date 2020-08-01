package com.zhoutao123.rpc.service.controller;

import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TestController {

  private final RpcRegistry rpcRegistry;

  public TestController(RpcRegistry rpcRegistry) {
    this.rpcRegistry = rpcRegistry;
  }

  @GetMapping("/method")
  public Map<String, List<NodeInfo>> test() {
    return rpcRegistry.getServiceNames();
  }
}
