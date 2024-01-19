package org.jingtao8a.client.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.jingtao8a.codec.RpcDecoder;
import org.jingtao8a.codec.RpcEncoder;

import java.util.concurrent.TimeUnit;

public class NettyClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(new RpcEncoder());
        socketChannel.pipeline().addLast(new RpcDecoder());
        socketChannel.pipeline().addLast(new NettyClientHandler());
    }
}
