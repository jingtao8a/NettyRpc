package org.jingtao8a.exception;

import org.jingtao8a.consts.enums.RpcErrorMsgEnum;

public class RpcException extends RuntimeException{
    public RpcException(RpcErrorMsgEnum rpcResponseErrorMsgEnum, String detail) {
        super(rpcResponseErrorMsgEnum.getMessage() + ":" + detail);
    }

    public RpcException(RpcErrorMsgEnum rpcExceptionErrorMsgEnum) {
        super(rpcExceptionErrorMsgEnum.getMessage());
    }
}
