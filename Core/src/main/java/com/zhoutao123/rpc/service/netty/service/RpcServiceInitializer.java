package com.zhoutao123.rpc.service.netty.service;

import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.entity.RpcRequest;
import com.zhoutao123.rpc.service.netty.coder.RpcDecoder;
import com.zhoutao123.rpc.service.netty.coder.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Netty 服务channel定义
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @since 0.0.1
 */
public class RpcServiceInitializer extends ChannelInitializer<SocketChannel> {

  private RpcServiceContext rpcServiceContext;

  public RpcServiceInitializer(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast(new RpcDecoder(RpcRequest.class));
    pipeline.addLast(new RpcEncoder());
    pipeline.addLast(new RpcRequestHandler(rpcServiceContext));
  }
}
