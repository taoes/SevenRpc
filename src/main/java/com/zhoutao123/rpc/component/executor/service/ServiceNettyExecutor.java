package com.zhoutao123.rpc.component.executor.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.base.Executor;
import com.zhoutao123.rpc.base.config.RpcConfig;
import com.zhoutao123.rpc.component.context.RpcServiceContext;
import com.zhoutao123.rpc.service.netty.service.RpcServiceInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** InitFunction 执行器 */
@Order(4)
@Component("serviceNettyExecutor")
public class ServiceNettyExecutor implements Executor {

  private static final Log log = LogFactory.get();

  @Autowired private RpcConfig rpcConfig;

  @Autowired private RpcServiceContext rpcServiceContext;

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
      log.info("RpcService started on port(s): {} (TCP)", rpcConfig.getPort());
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      log.error("Init ServiceRpc happen exception:", e);
      System.exit(500);
    } finally {
      boss.shutdownGracefully();
      work.shutdownGracefully();
    }
  }
}
