package com.zhoutao123.rpc.entity;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import java.io.Serializable;

/** 响应信息实体 */
public class ResponseInfo implements Serializable {

  private String methodName;

  private int status;

  private String result;

  public static ResponseInfo ok(String methodName, Object result) {

    ResponseInfo responseInfo = new ResponseInfo();
    responseInfo.methodName = methodName;
    responseInfo.status = HttpStatus.HTTP_OK;
    responseInfo.result = JSON.toJSONString(result);
    return responseInfo;
  }

  public static ResponseInfo error(String methodName, Object result) {
    ResponseInfo responseInfo = new ResponseInfo();
    responseInfo.methodName = methodName;
    responseInfo.status = HttpStatus.HTTP_BAD_METHOD;
    responseInfo.result = JSON.toJSONString(result);
    return responseInfo;
  }

  public String getMethodName() {
    return methodName;
  }

  public Integer getStatus() {
    return status;
  }

  public String getResult() {
    return result;
  }
}
