package com.zhoutao123.rpc.utils;

import java.io.IOException;
import java.io.InputStream;

/** 获取某个类所有的方法 */
public class LogoUtils {

  private LogoUtils() {}

  public static void printLogo() {
    StringBuilder builder = new StringBuilder();
    try (InputStream resourceAsStream = LogoUtils.class.getResourceAsStream("/logo.txt")) {
      int temp = 0;
      while ((temp = resourceAsStream.read()) != -1) {
        builder.append((char) temp);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (builder.length() > 0) {
      System.out.println(builder.toString());
    }
  }
}
