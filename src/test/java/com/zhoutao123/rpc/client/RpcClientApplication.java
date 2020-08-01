package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.base.annotation.EnableRpcClient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableRpcClient(scanPath = "com.zhoutao123.rpc.client")
@SpringBootApplication
@RequestMapping
@RestController
public class RpcClientApplication {

  @Autowired private ClientService clientService;

  public static void main(String[] args) {
    SpringApplication.run(RpcClientApplication.class, args);
  }

  @GetMapping("/test")
  public Map<String, Serializable> name(String name) {
    String name2 = clientService.getName(name);
    Map<String, Integer> sum = clientService.sum(3, 4);
    Map<String, Serializable> stringObjectMap = new HashMap<>(sum);
    stringObjectMap.put("大写", name2);
    return stringObjectMap;
  }
}
