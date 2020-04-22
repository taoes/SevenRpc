package com.zhoutao123.rpc.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** 获取某个类所有的方法 */
public class ClassUtils {

  private ClassUtils() {}

  private static final Map<Class<?>, Set<String>> cacheMap;

  // 享元模式
  static {
    cacheMap = new ConcurrentHashMap<>();
    allMethodOfClass(Object.class);
  }

  public static List<Method> allMethodOfClass(Class<?> aClass) {
    if (aClass == null) {
      return new ArrayList<>(0);
    }
    Method[] methods = aClass.getMethods();
    return Arrays.asList(methods);
  }

  public static synchronized Set<String> allMethodNameOfClass(Class<?> aClass) {
    Set<String> value = cacheMap.get(aClass);
    if (value != null) {
      return value;
    }

    value = allMethodOfClass(aClass).stream().map(Method::getName).collect(Collectors.toSet());
    cacheMap.put(aClass, value);
    return value;
  }
}
