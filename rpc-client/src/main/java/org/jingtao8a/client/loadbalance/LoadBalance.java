package org.jingtao8a.client.loadbalance;

import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.extension.SPI;

import java.util.List;

@SPI(value="random")
public interface LoadBalance {
    default String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest) {
        if (serviceAddress.isEmpty()) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress, rpcRequest);
    }

    String doSelect(List<String> serviceAddress, RpcRequest rpcRequest);
}
