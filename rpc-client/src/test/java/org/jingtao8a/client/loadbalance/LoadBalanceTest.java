package org.jingtao8a.client.loadbalance;

import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.extension.ExtensionLoader;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LoadBalanceTest {
    @Test
    public void test() {
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("full-round");
        System.out.println(loadBalance.getClass().getName());

        List<String> serviceAddress = new ArrayList<>();
        serviceAddress.add("127.0.0.1:121");
        serviceAddress.add("127.0.0.1:122");
        serviceAddress.add("127.0.0.1:123");
        serviceAddress.add("127.0.0.1:124");
        for (int i = 0; i < 8; i++) {
            System.out.println(loadBalance.doSelect(serviceAddress, null));
        }
    }
}
