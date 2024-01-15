package org.jingtao8a.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws  Exception {
        System.out.println("服务端建立连接，触发active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws  Exception {
        System.out.println("服务器收到消息： " + msg.toString());
        ctx.write(msg + "msg");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
