package com.zhoutao123.rpc.service.netty.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.client.Beat;
import com.zhoutao123.rpc.component.context.RpcServiceContext;
import com.zhoutao123.rpc.entity.MethodInfo;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.entity.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.util.StringUtils;

/** 服务端处理 RpcRequestHandle */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

  private final Log log = LogFactory.get();

  private final Map<String, MethodInfo> methodPool;

  public RpcRequestHandler(RpcServiceContext rpcServiceContext) {
    this.methodPool = rpcServiceContext.getMethodPool();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, RpcRequest request) {
    String requestId = request.getRequestId();

    if (Beat.BEAT_ID.equals(requestId)) {
      return;
    }

    // 构建一个RPC响应
    RpcResponse rpcResponse = new RpcResponse();
    rpcResponse.setRequestId(requestId);

    MethodInfo methodInfo = methodPool.get(request.getMethodName());
    if (methodInfo == null) {
      writeErrorPage(context, rpcResponse, "请求的方法不存在");
      return;
    }

    Method method = methodInfo.getMethod();
    Object instance = methodInfo.getInstance();
    Object[] objParams = request.getParameters();

    String errorMsg = validateMethod(objParams, method.getParameterCount());
    if (StringUtils.hasText(errorMsg)) {
      writeErrorPage(context, rpcResponse, errorMsg);
      return;
    }

    try {
      Object invokeResult = method.invoke(instance, objParams);
      rpcResponse.setResult(invokeResult);
      context.channel().writeAndFlush(rpcResponse);
      log.info("数据发送完成：{}", rpcResponse);
    } catch (Exception e) {
      writeErrorPage(context, rpcResponse, e.getMessage());
    }
  }

  /** 发生异常时候关闭连接 */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    Channel channel = ctx.channel();
    ctx.close();
    log.warn("连接:{} 发生异常:{}", channel.id(), cause);
  }

  private String validateMethod(Object[] objParams, int paramLength) {
    if (objParams != null && paramLength != objParams.length) {
      return "请求失败, 参数长度不匹配";
    } else if (objParams == null && paramLength != 0) {
      return "请求失败, 缺少请求参数";
    }
    return null;
  }

  /** 写入错误信息 */
  private void writeErrorPage(ChannelHandlerContext ctx, RpcResponse rpcResponse, String message) {
    log.error("请求发生错误，错误信息:{}", message);
    rpcResponse.setError(message);
    ctx.channel().writeAndFlush(rpcResponse);
  }
}
