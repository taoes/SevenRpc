package com.zhoutao123.rpc.service.netty.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;

/**
 * 服务端处理RPC请求
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @since 0.0.1
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

  private final Log log = LogFactory.get();

  private final RpcServiceContext rpcServiceContext;

  public RpcRequestHandler(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, RpcRequest request) {
    log.trace("接收到数据:{}", request);

    String requestId = request.getRequestId();

    RpcResponse rpcResponse = new RpcResponse();
    rpcResponse.setRequestId(requestId);

    MethodInfo methodInfo = rpcServiceContext.getMethodPool().get(request.getMethodName());
    if (methodInfo == null) {
      writeErrorPage(context, requestId, "请求的方法不存在");
      return;
    }

    Method method = methodInfo.getMethod();
    Object instance = methodInfo.getInstance();

    Object[] objParams = request.getParameters();

    if (objParams != null && method.getParameterCount() != objParams.length) {
      writeErrorPage(context, requestId, "参数长度不匹配");
      return;
    }

    Object invokeResult = null;
    try {
      invokeResult = method.invoke(instance, objParams);
      rpcResponse.setResult(invokeResult);
      context.channel().writeAndFlush(rpcResponse);
      log.info("数据发送完成：{}", rpcResponse);
    } catch (Exception e) {
      writeErrorPage(context, requestId, e);
    }
  }

  /** 发生异常时候关闭连接 */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
    log.warn("发生异常:{}", cause);
  }

  /** 写入错误信息 */
  private void writeErrorPage(ChannelHandlerContext context, String requestId, Exception e) {
    writeErrorPage(context, requestId, e.getMessage());
  }

  /** 写入错误信息 */
  private void writeErrorPage(ChannelHandlerContext context, String requestId, String message) {
    RpcResponse error = new RpcResponse();
    error.setError(message);
    error.setRequestId(requestId);

    context.channel().writeAndFlush(error);
    context.close();
  }
}
