package com.zhoutao123.rpc.entity;

import java.io.Serializable;
import java.util.List;

/** 请求信息实体 */
public class RequestInfo implements Serializable {

  private String methodName;

  private List<String> params;

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public List<String> getParams() {
    return params;
  }

  public void setParams(List<String> params) {
    this.params = params;
  }
}
