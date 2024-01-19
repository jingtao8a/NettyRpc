package org.jingtao8a.client.loadbalance.loadbalancer;

import org.jingtao8a.client.loadbalance.LoadBalance;
import org.jingtao8a.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
