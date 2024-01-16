package org.jingtao8a.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServerThread extends Thread{
    @Override
    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //禁用Nagle算法
                .childOption(ChannelOption.TCP_NODELAY, true)
                //开启TCP底层的心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //表示系统用于临时存放已完成三次握手的请求的队列的最大长度
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new NettyServerChannelInitializer());

    }
}
