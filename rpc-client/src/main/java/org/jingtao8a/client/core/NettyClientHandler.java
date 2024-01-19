package org.jingtao8a.client.core;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.consts.RpcConstants;
import org.jingtao8a.consts.enums.MessageTypeEnum;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcResponse;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage) throws Exception {
        log.info("client receive msg:[{}]", rpcMessage);
        byte messageType = rpcMessage.getMessageType();
        if (messageType == MessageTypeEnum.HEARTBEAT_RESPONSE_TYPE) {
            log.debug("heart receive[{}]", rpcMessage.getBody());
        } else {
            RpcResponse rpcResponse = (RpcResponse) rpcMessage.getBody();
            PendingRpcRequests.complete(rpcResponse);
        }
    }

    /**
     * 当5秒内没有主动远程调用，也就是没有写事件发生时候，触发userEventTriggered主动写并发送心跳数据包
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setMessageType(MessageTypeEnum.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setSerializerType(SerializerTypeEnum.KRYO.getCode());
                rpcMessage.setCompress((byte)0);
                rpcMessage.setBody(RpcConstants.PING);
                channelHandlerContext.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(channelHandlerContext, evt);
        }
    }

    /**
     * 客户端异常捕获，并关闭连接, 从channel Map中删除channel, 下次调用自动重连server端
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
