package org.jingtao8a.client.faultTolerantInvoker;

import org.jingtao8a.client.core.NettyClient;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcResponse;

public class FailFastInvoker implements FaultTolerantInvoker {
    @Override
    public RpcResponse doInvoke(NettyClient nettyClient, RpcMessage rpcMessage, String targetServiceUrl, boolean isAsync) {
        return nettyClient.sendRequest(rpcMessage, targetServiceUrl, isAsync);
    }
}
