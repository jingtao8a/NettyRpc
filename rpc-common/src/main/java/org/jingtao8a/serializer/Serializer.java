package org.jingtao8a.serializer;

import org.jingtao8a.consts.enums.SerializerTypeEnum;


public interface Serializer {
    SerializerTypeEnum getSerializerAlgorithm();

    byte[] serialize(Object object);

    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
