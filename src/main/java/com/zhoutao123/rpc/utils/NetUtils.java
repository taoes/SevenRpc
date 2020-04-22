package com.zhoutao123.rpc.utils;

import java.net.InetAddress;

/**
 * 网络工具类
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @version 0.0.1
 */
public class NetUtils {

  private NetUtils() {}

  /**
   * 获取内网IP地址
   *
   * @return 局域网IP地址 eg: 192.168.1.101
   */
  public static String getIntranetIp() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {
      return null;
    }
  }
}
