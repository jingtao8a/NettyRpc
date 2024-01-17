package org.jingtao8a.exception;

import org.jingtao8a.consts.enums.RpcResponseErrorMsgEnum;

public class RpcException extends RuntimeException{
    public RpcException(RpcResponseErrorMsgEnum rpcResponseErrorMsgEnum, String detail) {
        super(rpcResponseErrorMsgEnum.getMessage() + ":" + detail);
    }

    public RpcException(RpcResponseErrorMsgEnum rpcExceptionErrorMsgEnum) {
        super(rpcExceptionErrorMsgEnum.getMessage());
    }
}
