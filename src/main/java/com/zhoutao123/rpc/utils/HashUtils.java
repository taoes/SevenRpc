package com.zhoutao123.rpc.utils;

import java.security.MessageDigest;

public class HashUtils {

  /** @return hex string with upper case */
  public static String md5(String source) {
    String des = "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] result = md.digest(source.getBytes());
      StringBuilder buf = new StringBuilder();
      for (byte b : result) {
        buf.append(String.format("%02X", b));
      }
      des = buf.toString().toLowerCase();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("md5 failure");
    }
    return des;
  }
}
