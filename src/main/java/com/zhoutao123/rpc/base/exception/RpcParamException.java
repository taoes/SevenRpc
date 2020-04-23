package com.zhoutao123.rpc.base.exception;

public class RpcParamException extends RpcBaseException {

  private String message;

  public RpcParamException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
