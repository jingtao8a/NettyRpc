package org.jingtao8a.client.faultTolerantInvoker;

import org.jingtao8a.client.core.NettyClient;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.extension.SPI;

@SPI(value="fail-fast")
public interface FaultTolerantInvoker {
    RpcResponse doInvoke(NettyClient nettyClient, RpcMessage rpcMessage, String targetServiceUrl, boolean isAsync);
}
