package com.zhoutao123.rpc.component.executor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.RpcServiceContext;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.service.netty.service.RpcServiceInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/** InitFunction 执行器 */
@Component("nettyExecutor")
public class NettyExecutor implements Executor {

  private RpcConfig rpcConfig;

  private static Log log = LogFactory.get();

  private RpcServiceContext rpcServiceContext;

  public NettyExecutor(RpcConfig rpcConfig, RpcServiceContext rpcServiceContext) {
    this.rpcConfig = rpcConfig;
    this.rpcServiceContext = rpcServiceContext;
  }

  /** 启动Netty服务 */
  public void start() {
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup work = new NioEventLoopGroup();
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
      serverBootstrap
          .group(boss, work)
          .channel(NioServerSocketChannel.class)
          .childHandler(new RpcServiceInitializer(rpcServiceContext));
      ChannelFuture future = serverBootstrap.bind(rpcConfig.getPort()).sync();
      log.info("Netty服务启动在:{} 端口", rpcConfig.getPort());
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      log.info("初始化Netty服务发生异常.", e);
      System.exit(500);
    } finally {
      boss.shutdownGracefully();
      work.shutdownGracefully();
    }
  }
}