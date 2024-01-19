package org.jingtao8a.client.faultTolerantInvoker;

import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.client.core.NettyClient;
import org.jingtao8a.consts.enums.RpcErrorMsgEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.exception.RpcException;

@Slf4j
public class RetryInvoker implements FaultTolerantInvoker{
    /**
     * 默认重试次数
     */
    public static  int DEFAULT_RETRY_TIMES = 3;
    @Override
    public RpcResponse doInvoke(NettyClient nettyClient, RpcMessage rpcMessage, String targetServiceUrl, boolean isAsync) {
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                RpcResponse result = nettyClient.sendRequest( rpcMessage, targetServiceUrl,isAsync);
                if (result != null) {
                    return result;
                }
            } catch (RpcException ex) {
                log.error("invoke error. retry times=" + i, ex);
            }
        }
        throw new RpcException(RpcErrorMsgEnum.SERVICE_INVOCATION_FAILURE);
    }
}
