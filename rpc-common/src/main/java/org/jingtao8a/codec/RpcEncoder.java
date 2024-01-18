package org.jingtao8a.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jingtao8a.dto.RpcMessage;

public class RpcEncoder  extends MessageToByteEncoder<RpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        RpcCodec.INSTANCE.encode(rpcMessage, byteBuf);
    }
}
