package org.jingtao8a.serializer;

import org.jingtao8a.consts.enums.MessageTypeEnum;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.dto.*;
import org.jingtao8a.serializer.kryo.KryoSerializer;
import org.jingtao8a.serializer.hessian.HessianSerializer;
import org.jingtao8a.serializer.protostuff.ProtostuffSerializer;
import org.junit.jupiter.api.Test;

import static org.jingtao8a.consts.enums.RpcResponseCodeEnum.SUCCESS;
import static org.junit.Assert.assertEquals;

public class SerializerTest {
    private static RpcMessage buildMessage() {
        RpcResponse rpcResponse = new RpcResponse(SUCCESS.getCode(), SUCCESS.getMessage(), new String("我是结果，我是结果，我是结果"));
        RpcMessage rpcMessage = new RpcMessage(MessageTypeEnum.RESPONSE_TYPE, SerializerTypeEnum.KRYO.getCode(), (byte) 0, 1, rpcResponse);
        return rpcMessage;

    }

    public static void kryoSerializeSizeTest() {
        RpcMessage data = buildMessage();
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] serialize = kryoSerializer.serialize(data);
        System.out.println("kryo's size is " + serialize.length);
        RpcMessage out = kryoSerializer.deserialize(RpcMessage.class, serialize);
        assertEquals(out.toString(), data.toString());
    }

    public static void hessianSerializeSizeTest() {
        RpcMessage data = buildMessage();
        HessianSerializer hessianSerializer = new HessianSerializer();
        byte[] serialize = hessianSerializer.serialize(data);
        System.out.println("hessian's size is " + serialize.length);
        RpcMessage out = hessianSerializer.deserialize(RpcMessage.class, serialize);
        assertEquals(out.toString(), data.toString());
    }

    public static void protostuffSerializeSizeTest() {
        RpcMessage data = buildMessage();
        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        byte[] serialize = protostuffSerializer.serialize(data);
        System.out.println("protostuff's size is " + serialize.length);
        RpcMessage out = protostuffSerializer.deserialize(RpcMessage.class, serialize);
        assertEquals(out.toString(), data.toString());
    }
    @Test
    public void sizeTest() {
        kryoSerializeSizeTest();
        hessianSerializeSizeTest();
        protostuffSerializeSizeTest();
    }
}
