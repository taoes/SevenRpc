package com.zhoutao123.rpc.service.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.JSONValidator.Type;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import com.zhoutao123.rpc.entity.RequestInfo;
import com.zhoutao123.rpc.entity.ResponseInfo;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * RPC 请求处理Handler
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @since 0.0.1
 */
@Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RequestInfo> {

  private RpcServiceContext rpcServiceContext;

  public RpcRequestHandler(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, RequestInfo requestInfo) {

    // 查询方法信息
    MethodInfo methodInfo = rpcServiceContext.getMethodPool().get(requestInfo.getMethodName());
    if (methodInfo == null) {
      writeErrorPage(context, "请求的方法不存在");
      return;
    }

    Method method = methodInfo.getMethod();
    Object instance = methodInfo.getInstance();

    // 处理请求参数
    Object[] objParams = handleParams(requestInfo);

    // 判断参数信息
    if (method.getParameterCount() != objParams.length) {
      writeErrorPage(context, "参数长度不匹配");
      return;
    }

    Object invokeResult = null;
    try {
      invokeResult = method.invoke(instance, objParams);
      ResponseInfo ok = ResponseInfo.ok(methodInfo.getMethodName(), invokeResult);
      context.channel().writeAndFlush(ok);
    } catch (Exception e) {
      writeErrorPage(context, e);
    }
  }

  // 发生异常时候关闭连接
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
    ctx.close();
  }

  /** 写入错误信息 */
  private void writeErrorPage(ChannelHandlerContext context, Exception e) {
    writeErrorPage(context, e.getMessage());
  }

  /** 写入错误信息 */
  private void writeErrorPage(ChannelHandlerContext context, String message) {
    ResponseInfo error = ResponseInfo.error("", message);
    context.channel().writeAndFlush(error);
  }

  private Object[] handleParams(RequestInfo requestInfo) {
    List<String> params = requestInfo.getParams();
    List<Object> paramObjects = new ArrayList<>(params.size());
    for (String param : params) {
      if (param == null || param.equals("null")) {
        paramObjects.add(null);
        continue;
      }
      Type validate = JSONValidator.from(param).getType();
      if (validate == Type.Object) {
        paramObjects.add(JSON.parse(param));
      } else {
        paramObjects.add(param);
      }
    }
    return paramObjects.toArray();
  }
}
