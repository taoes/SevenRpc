package com.zhoutao123.rpc.test;

import com.zhoutao123.rpc.base.annotation.RpcService;

@RpcService
public interface BookService {

  String findByName(String name);
}
