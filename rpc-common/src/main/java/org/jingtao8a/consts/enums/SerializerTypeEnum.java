package org.jingtao8a.consts.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerTypeEnum {
    HESSIAN((byte)1, "hessian"),
    KRYO((byte)2, "kryo"),
    PROTOSTUFF((byte)3, "protostuff");

    private final byte code;
    private final String name;

    public static byte getCode(String name) {
        for (SerializerTypeEnum c : SerializerTypeEnum.values()) {
            if (c.getName().equals(name)) {
                return c.getCode();
            }
        }
        return HESSIAN.getCode();
    }

    public static String getName(byte code) {
        for (SerializerTypeEnum c : SerializerTypeEnum.values()) {
            if (c.getCode() == code) {
               return c.getName();
            }
        }
        return HESSIAN.getName();
    }
}
