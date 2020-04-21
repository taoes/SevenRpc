package com.zhoutao123.rpc.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 获取某个类所有的方法 */
public class ClassUtils {

  public static List<Method> allMethodOfClass(Class<?> aClass) {
    if (aClass == null) {
      return new HashSet<>(0);
    }
    Method[] methods = aClass.getMethods();
    return Arrays.asList(methods);
  }

  // FIXME: 使用缓存优化
  public static Set<String> allMethodNameOfClass(Class<?> aClass) {
    return allMethodOfClass(aClass).stream().map(Method::getName).collect(Collectors.toSet());
  }
}
