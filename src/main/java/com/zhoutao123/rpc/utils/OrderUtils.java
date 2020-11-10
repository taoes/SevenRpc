package com.zhoutao123.rpc.utils;

import java.util.Objects;
import org.springframework.core.annotation.Order;

public class OrderUtils {

  /** 解析InitFunction的顺序 */
  public static Integer resolverOrder(Class<?> clazz) {
    Order order = clazz.getAnnotation(Order.class);
    if (Objects.nonNull(order)) {
      return order.value();
    }
    return Integer.MAX_VALUE;
  }
}
