package com.zhoutao123.rpc.component;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.service.netty.NettyServiceHandle;
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

  Log log = LogFactory.get();

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
          .childHandler(new NettyServiceHandle());
      ChannelFuture future = serverBootstrap.bind(RpcConfig.SERVICE_PORT).sync();
      log.info("Netty服务启动在:{} 端口", RpcConfig.SERVICE_PORT);
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
