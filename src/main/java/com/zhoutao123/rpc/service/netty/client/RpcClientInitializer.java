package com.zhoutao123.rpc.service.netty.client;

import com.zhoutao123.rpc.entity.RpcResponse;
import com.zhoutao123.rpc.service.netty.coder.RpcDecoder;
import com.zhoutao123.rpc.service.netty.coder.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

  private final RpcClientHandler handler;

  final CountDownLatch latch;

  public RpcClientInitializer(CountDownLatch latch) {
    this.latch = latch;
    this.handler = new RpcClientHandler(latch);
  }

  public RpcClientHandler getHandler() {
    return handler;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new RpcDecoder<>(RpcResponse.class));
    pipeline.addLast(new RpcEncoder());
    pipeline.addLast(handler);
  }
}
