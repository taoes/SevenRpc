package com.zhoutao123.rpc.service.netty.client;

import com.zhoutao123.rpc.client.Beat;
import com.zhoutao123.rpc.component.client.RpcFuture;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.entity.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

  private volatile Channel channel;

  private final ConcurrentHashMap<String, RpcFuture> pendingRPC = new ConcurrentHashMap<>();

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    this.channel = ctx.channel();
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    super.channelRegistered(ctx);
    this.channel = ctx.channel();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
    log.trace("接收到数据:{}", msg);
    String requestId = msg.getRequestId();
    RpcFuture rpcFuture = pendingRPC.get(requestId);
    if (rpcFuture != null) {
      rpcFuture.done(msg);
      pendingRPC.remove(requestId);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.info("RpcClientHandler.exceptionCaught:{}", ctx.channel().id());
    ctx.close();
  }

  public RpcFuture sendRequest(RpcRequest rpcRequest) throws InterruptedException {
    RpcFuture future = new RpcFuture(rpcRequest);
    String requestId = rpcRequest.getRequestId();

    pendingRPC.put(requestId, future);
    ChannelFuture channelFuture = channel.writeAndFlush(rpcRequest).sync();
    if (!channelFuture.isSuccess()) {
      log.error("Send request {} error", requestId);
    }

    return future;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      sendRequest(Beat.BEAT_PING);
      log.info("发送心跳完成....");
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}
