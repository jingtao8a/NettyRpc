package org.jingtao8a.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.register.Register;
import org.jingtao8a.server.invoke.Invoker;

import java.net.InetSocketAddress;

@Slf4j
@Setter
@Getter
public class NettyServerThread extends Thread{
    private Register register;
    private Invoker invoker;
    private ServiceRegisterCache serviceRegisterCache;
    private InetSocketAddress serverAddress;//服务端开放的地址端口
    @Override
    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //禁用Nagle算法
                .childOption(ChannelOption.TCP_NODELAY, true)
                //开启TCP底层的心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //表示系统用于临时存放已完成三次握手的请求的队列的最大长度
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new NettyServerChannelInitializer(invoker, serviceRegisterCache));

            // bind操作(对应初始化)是异步的，通过sync改为同步等待初始化的完成，否则立即操作对象(未初始完全)可能会报错
            ChannelFuture f = bootstrap.bind(serverAddress.getAddress(), serverAddress.getPort()).sync();
            log.info("Netty Server started on address {}", serverAddress);
            if (register != null) {
                register.registerServiceMap(serviceRegisterCache.getServiceMap(), serverAddress);
            } else {
                log.warn("ServiceRegistry cannot be found and started");
            }
            // 不会立即执行 finally，而阻塞在这里，等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            register.unregisterAllService(serverAddress);
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
