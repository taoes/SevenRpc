package com.zhoutao123.rpc.service.impl;

import com.zhoutao123.rpc.base.annotation.RpcService;
import com.zhoutao123.rpc.test.BookService;

@RpcService
public class BookServiceImpl implements BookService {

  @Override
  public String findByName(String name) {
    return name.toUpperCase();
  }
}
