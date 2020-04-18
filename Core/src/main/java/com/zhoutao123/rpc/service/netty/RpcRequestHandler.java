package com.zhoutao123.rpc.service.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

/**
 * RPC 请求处理Handler
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @since 0.0.1
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

  @Override
  protected void channelRead0(
      ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
    // 1、接收到方法

    // 2、从上下文中获取该方法

    // 3、执行方法

    // 4、序列化时数据

    // 5、返回相应数据
  }
}
