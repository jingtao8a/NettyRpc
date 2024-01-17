package org.jingtao8a.codec;

import io.netty.buffer.ByteBuf;
import org.jingtao8a.consts.RpcConstants;
import org.jingtao8a.consts.enums.MessageTypeEnum;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.extension.ExtensionLoader;
import org.jingtao8a.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义协议
 * 0     1     2     3     4        5     6     7     8    9            10             11      12  13 14 15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-------------+--------------+--------+--+--+--+--+
 * |   magic   code        |version |      full length    | messageType| serializerType|compress| RequestId |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B serializerType（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 *
 * */

public class RpcCodec {
    private final Map<Byte, Class<?>> messageTypeMap;

    public RpcCodec() {
        messageTypeMap = new HashMap<>();
        messageTypeMap.put(MessageTypeEnum.REQUEST_TYPE, RpcRequest.class);
        messageTypeMap.put(MessageTypeEnum.RESPONSE_TYPE, RpcResponse.class);
    }

    public Object decode(ByteBuf in) {
        for (int i = 1; i <= 5; ++i) {
            in.readByte();
        }
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte serializerTypeCode = in.readByte();
        byte compress = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = new RpcMessage(messageType, serializerTypeCode, compress, requestId, null);
        //心跳类型，body长度为0
        if (messageType == MessageTypeEnum.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setBody(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == MessageTypeEnum.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setBody(RpcConstants.PONG);
            return rpcMessage;
        }
        //获取数据体body的长度
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        byte[] bs = new byte[bodyLength];
        in.readBytes(bs);
        //反压缩 skip
        //反序列化
        String serializerType = SerializerTypeEnum.getName(serializerTypeCode);
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerType);
        Object body = serializer.deserialize(messageTypeMap.get(messageType), bs);
        rpcMessage.setBody(body);
        return rpcMessage;
    }

    public ByteBuf encode(RpcMessage rpcMessage, ByteBuf out) {
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        out.writeByte(RpcConstants.VERSION);
        out.writerIndex(out.writerIndex() + 4);//writerindex + 4
        byte messageType = rpcMessage.getMessageType();
        out.writeByte(messageType);
        out.writeByte(rpcMessage.getSerializerType());
        out.writeByte(rpcMessage.getCompress());
        out.writeInt(rpcMessage.getRequestId());
        // 写body，并获取数据长度
        byte[] bodyBytes = null;
        int fullLength = RpcConstants.HEAD_LENGTH;
        if (messageType != MessageTypeEnum.HEARTBEAT_REQUEST_TYPE &&
                messageType != MessageTypeEnum.HEARTBEAT_RESPONSE_TYPE) {
            //序列化
            String serializerName = SerializerTypeEnum.getName(rpcMessage.getSerializerType());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerName);
            bodyBytes = serializer.serialize(rpcMessage.getBody());
            //压缩 skip
            fullLength += bodyBytes.length;
        }
        if (bodyBytes != null) {
            out.writeBytes(bodyBytes);
        }
        //记录当前写指针
        int writerIndex = out.writerIndex();
        out.writerIndex(RpcConstants.MAGIC_LENGTH + RpcConstants.VERSION_LENGTH);
        out.writeInt(fullLength);
        //写指针复原
        out.writerIndex(writerIndex);
        return out;
    }
}
