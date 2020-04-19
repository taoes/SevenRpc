package com.zhoutao123.rpc.service.netty;

import com.zhoutao123.rpc.base.RpcServiceContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Netty 服务channel定义
 *
 * @author Seven zhoutao825638@vip.qq.com
 * @since 0.0.1
 */
public class NettyServiceHandle extends ChannelInitializer<SocketChannel> {

  private RpcServiceContext rpcServiceContext;

  public NettyServiceHandle(RpcServiceContext rpcServiceContext) {
    this.rpcServiceContext = rpcServiceContext;
  }

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    // 转换为字符串
    pipeline.addLast(new StringDecoder());
    pipeline.addLast(new StringEncoder());

    // 转换为请求信息
    pipeline.addLast(new ResponseDecode());
    pipeline.addLast(new ResponseEncode());

    // 业务逻辑处理
    pipeline.addLast(new RpcRequestHandler(rpcServiceContext));
  }
}
