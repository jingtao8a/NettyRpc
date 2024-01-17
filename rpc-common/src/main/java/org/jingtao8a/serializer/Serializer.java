package org.jingtao8a.serializer;

import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.extension.SPI;

@SPI(value="kryo")
public interface Serializer {
    SerializerTypeEnum getSerializerAlgorithm();

    byte[] serialize(Object object);

    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
