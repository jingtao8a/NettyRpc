package org.jingtao8a.serializer.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.exception.SerializerException;
import org.jingtao8a.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {

    @Override
    public SerializerTypeEnum getSerializerAlgorithm() {
        return SerializerTypeEnum.HESSIAN;
    }

    @Override
    public byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializerException("Hessian Serialization failed:", e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            T res = null;
            res = (T)hessianInput.readObject();
            return res;
        } catch (Exception e) {
            throw new SerializerException("Hessian Deserialization failed:", e.getMessage());
        }
    }
}
