package com.zhoutao123.rpc.service.controller;

import com.zhoutao123.rpc.base.registry.RpcRegistry;
import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TestController {

  @Autowired private RpcRegistry registry;

  @GetMapping("/service")
  public Map<String, Set<NodeInfo>> test() {
    return registry.getServiceNames();
  }
}
