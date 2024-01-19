package org.jingtao8a.client.loadbalance.loadbalancer;

import org.jingtao8a.client.loadbalance.LoadBalance;
import org.jingtao8a.dto.RpcRequest;

import java.util.List;

/**
 *  轮询
 */
public class FullRoundLoadBalance implements LoadBalance {
    private int index = 0;
    @Override
    public String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {
        if (serviceAddress.size() == index) {
            index = 0;
        }
        return serviceAddress.get(index++);
    }
}
