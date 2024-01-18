package org.jingtao8a.consts.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcResponseCodeEnum {
    SUCCESS(200, "success"),
    FAIL(500, "fail");

    private final int code;
    private final String message;
}
