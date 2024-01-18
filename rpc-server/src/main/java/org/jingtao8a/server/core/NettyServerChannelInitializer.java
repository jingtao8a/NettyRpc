package org.jingtao8a.server.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.AllArgsConstructor;
import org.jingtao8a.codec.RpcDecoder;
import org.jingtao8a.codec.RpcEncoder;
import org.jingtao8a.server.invoke.Invoker;
import org.jingtao8a.util.threadpool.ThreadPoolFactoryUtil;
import sun.nio.ch.Net;

import java.util.concurrent.TimeUnit;


public class NettyServerChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private Invoker invoker;
    private ServiceRegisterCache serviceRegisterCache;
    private DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2,
            ThreadPoolFactoryUtil.createThreadFactory("org.jingtao8a.service-handler-group", false));
    public NettyServerChannelInitializer(Invoker invoker, ServiceRegisterCache serviceRegisterCache) {
        this.invoker = invoker;
        this.serviceRegisterCache = serviceRegisterCache;
    }
    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        // 心跳，空闲检测
        nioSocketChannel.pipeline().addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
        // 处理粘贴包
        nioSocketChannel.pipeline().addLast(new RpcDecoder());
        nioSocketChannel.pipeline().addLast(new RpcEncoder());
        nioSocketChannel.pipeline().addLast(serviceHandlerGroup, new NettyServerHandler(invoker, serviceRegisterCache));
    }
}
