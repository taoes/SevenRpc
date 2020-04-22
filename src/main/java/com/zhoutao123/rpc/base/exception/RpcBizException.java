package com.zhoutao123.rpc.base.exception;

public class RpcBizException extends RpcBaseException {

  private String message;

  public RpcBizException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
