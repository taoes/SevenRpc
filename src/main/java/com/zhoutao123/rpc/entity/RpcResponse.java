package com.zhoutao123.rpc.entity;

import lombok.Data;

/**
 * RPC Response
 *
 * @author huangyong
 */
@Data
public class RpcResponse {

  // 请求ID
  private String requestId;

  // 错误信息
  private String error;

  // 响应信息
  private Object result;
}
