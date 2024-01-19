package org.jingtao8a.async;

import org.jingtao8a.consts.enums.RpcErrorMsgEnum;
import org.jingtao8a.consts.enums.RpcResponseCodeEnum;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.exception.RpcException;

public abstract class ResponseCallback {
    public void success(RpcResponse response) {
        AsyncCallBackExecutor.executor(()->{
            if (response.getCode() == RpcResponseCodeEnum.SUCCESS.getCode()) {
                try {
                    callBack(response);
                } catch (Exception e) {
                    onException(response, e);
                }
            } else {
                onException(response, new RpcException(RpcErrorMsgEnum.SERVICE_INVOCATION_FAILURE));
            }
        });
    }

    public abstract void callBack(RpcResponse result);

    public abstract void onException(RpcResponse result, Exception e);
}
