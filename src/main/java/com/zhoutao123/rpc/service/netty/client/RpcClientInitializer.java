package com.zhoutao123.rpc.service.netty.client;

import com.zhoutao123.rpc.entity.RpcResponse;
import com.zhoutao123.rpc.service.netty.coder.RpcDecoder;
import com.zhoutao123.rpc.service.netty.coder.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

  @Getter private final RpcClientHandler handler;

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
    pipeline.addLast(new RpcDecoder<>(RpcResponse.class));
    pipeline.addLast(new RpcEncoder());
    pipeline.addLast(this.handler);
  }
}
