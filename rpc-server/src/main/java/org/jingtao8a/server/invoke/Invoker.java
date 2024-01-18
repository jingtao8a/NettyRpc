package org.jingtao8a.server.invoke;

import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.extension.SPI;

@SPI(value="jdk")
public interface Invoker {
    Object invoke(RpcRequest rpcRequest, Object service);
}
