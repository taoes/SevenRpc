package com.zhoutao123.rpc.base.exception;

public class RpcTimeoutException extends RpcBaseException {

  private String message;

  public RpcTimeoutException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
