package com.zhoutao123.rpc.service.netty.client;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.CountDownLatch;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

  final CountDownLatch latch;

  private final Log log = LogFactory.get();

  public RpcResponse response;

  public RpcClientHandler(CountDownLatch latch) {
    this.latch = latch;
  }


  public RpcResponse getResponse() {
    return response;
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {}

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
    log.trace("接收到数据:{}", msg);
    this.response = msg;
    latch.countDown();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.info("RpcClientHandler.exceptionCaught:{}", ctx.channel().id());
    this.response = new RpcResponse();
    this.response.setError(cause.getMessage());
    this.response.setResult(cause);
    latch.countDown();
  }
}
