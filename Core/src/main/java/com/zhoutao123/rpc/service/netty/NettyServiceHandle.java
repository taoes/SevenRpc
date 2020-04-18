package com.zhoutao123.rpc.service.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Netty 服务channel定义
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @since 0.0.1
 */
public class NettyServiceHandle extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast(new HttpServerCodec());
    // 开启Gzip
    pipeline.addLast(new HttpContentCompressor());

    // 处理RPC 请求
    pipeline.addLast(new RpcRequestHandler());
  }
}
