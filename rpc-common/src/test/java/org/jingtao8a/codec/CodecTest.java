package org.jingtao8a.codec;

import io.netty.buffer.Unpooled;
import org.jingtao8a.consts.enums.MessageTypeEnum;
import org.jingtao8a.consts.enums.RpcResponseCodeEnum;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import org.junit.Test;
import static org.junit.Assert.*;

public class CodecTest {
    @Test
    public void test() {
        RpcCodec rpcCodec = new RpcCodec();
        RpcMessage rpcMessage = new RpcMessage(MessageTypeEnum.RESPONSE_TYPE, SerializerTypeEnum.KRYO.getCode(),
                (byte)0, 1234,
                new RpcResponse(RpcResponseCodeEnum.SUCCESS.getCode(), RpcResponseCodeEnum.SUCCESS.getMessage(), "success"));
        ByteBuf byteBuf = Unpooled.buffer(2560);
        rpcCodec.encode(rpcMessage, byteBuf);
        RpcMessage res = (RpcMessage) rpcCodec.decode(byteBuf);
        System.out.println(res.toString());
        assertEquals(rpcMessage.toString(), res.toString());
    }
}
