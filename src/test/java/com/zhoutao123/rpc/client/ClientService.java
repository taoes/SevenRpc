package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.base.annotation.RpcConsumer;
import com.zhoutao123.rpc.test.BookService;
import com.zhoutao123.rpc.test.TestService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

  @RpcConsumer
  BookService bookService;

  @RpcConsumer
  TestService testService;

  public ClientService(BookService bookService, TestService testService) {
    this.bookService = bookService;
    this.testService = testService;
  }

  public String getName(String name) {
    return bookService.findByName(name);
  }

  public Map<String, Integer> sum(int x, int y) {
    return testService.result(x, y);
  }
}
