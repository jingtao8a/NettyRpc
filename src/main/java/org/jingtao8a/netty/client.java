package org.jingtao8a.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


public class client extends Thread{
    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());

        try {

            ChannelFuture future = bootstrap.connect("127.0.0.1", 8090).sync();
            System.out.println("S2 : 客户端构建完成 .....");

            for (int i = 0; i < 10; i++) {
                String message = "第" + i + "条消息 ， " + "Hello ";
                future.channel().writeAndFlush(message);
                Thread.sleep(1000);
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
