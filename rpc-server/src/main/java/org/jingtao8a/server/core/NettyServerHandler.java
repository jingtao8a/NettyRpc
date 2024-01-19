package org.jingtao8a.server.core;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.consts.RpcConstants;
import org.jingtao8a.consts.enums.MessageTypeEnum;
import org.jingtao8a.consts.enums.RpcResponseCodeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.server.invoke.Invoker;
import org.jingtao8a.util.ServiceUtil;

@AllArgsConstructor
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private Invoker invoker;
    private ServiceRegisterCache serviceRegisterCache;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage) throws Exception {
        byte messageType = rpcMessage.getMessageType();
        //如果是心跳消息，回复pong
        if (messageType == MessageTypeEnum.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setMessageType(MessageTypeEnum.HEARTBEAT_RESPONSE_TYPE);
            rpcMessage.setBody(RpcConstants.PONG);
        } else {
            RpcRequest rpcRequest = (RpcRequest) rpcMessage.getBody();
            // 根据请求参数，找到对应的服务，反射执行方法
            Object result = handle(rpcRequest);
            log.info("server get result {} ", result.toString());
            rpcMessage.setMessageType(MessageTypeEnum.RESPONSE_TYPE);
            if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable()) {
                RpcResponse rpcResponse = new RpcResponse(rpcRequest.getRequestId(), RpcResponseCodeEnum.SUCCESS.getCode(), RpcResponseCodeEnum.SUCCESS.getMessage(), result);
                rpcMessage.setBody(rpcResponse);
            } else {
                RpcResponse rpcResponse = new RpcResponse(null, RpcResponseCodeEnum.FAIL.getCode(), RpcResponseCodeEnum.FAIL.getMessage(), null);
                rpcMessage.setBody(rpcResponse);
                log.error("not writable now, message dropped");
            }
        }
        channelHandlerContext.writeAndFlush(rpcMessage).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                    log.error("Fail!! Send response for request " + rpcMessage.getRequestId());

                } else {
                    log.info("Send response for request " + rpcMessage.getRequestId());
                }
            }
        });
    }

    /**
     * 15s没有收到客户端请求，直接关闭连接
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.debug("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
    private Object handle(RpcRequest rpcRequest) {
        String className = rpcRequest.getClassName();
        String version = rpcRequest.getVersion();
        String serviceKey = ServiceUtil.makeServiceKey(className, version);
        Object serviceBean = serviceRegisterCache.getService(serviceKey);
        if (serviceBean == null) {
            log.error("Can not find org.jingtao8a.service implement with interface name: {} and version: {}", className, version);
        }
        return invoker.invoke(rpcRequest, serviceBean);
    }
}
