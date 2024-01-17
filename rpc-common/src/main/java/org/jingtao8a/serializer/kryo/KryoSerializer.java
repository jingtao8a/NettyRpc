package org.jingtao8a.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.exception.SerializerException;
import org.jingtao8a.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {
    /* kryo是线程不安全的 */
    private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo= new Kryo();
//            kryo.register(RpcResponse.class);
//            kryo.register(RpcRequest.class);
            return kryo;
        }
    };

    @Override
    public SerializerTypeEnum getSerializerAlgorithm() {
        return SerializerTypeEnum.KRYO;
    }

    @Override
    public byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializerException("Kryo Serialization failed:", e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            T res = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return res;
        } catch (Exception e) {
            throw new SerializerException("Kryo Deserialization failed:", e.getMessage());
        }
    }
}
