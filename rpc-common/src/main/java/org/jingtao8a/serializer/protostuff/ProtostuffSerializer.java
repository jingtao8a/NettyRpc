package org.jingtao8a.serializer.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.serializer.Serializer;

public class ProtostuffSerializer implements Serializer {

    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    @Override
    public SerializerTypeEnum getSerializerAlgorithm() {
        return SerializerTypeEnum.PROTOSTUFF;
    }

    @Override
    public byte[] serialize(Object object) {
        Class<?> clazz = object.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtobufIOUtil.toByteArray(object, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }

        return bytes;
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
