package org.jingtao8a.register.zk;

import org.jingtao8a.register.Register;
import org.jingtao8a.register.RegisterFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ZkRegisterFactory implements RegisterFactory {
    private static final Map<String, ZkRegister> cache = new ConcurrentHashMap<>();

    /**
     *
     * @param address 注册中心地址
     * @return
     */
    @Override
    public Register getRegister(String address) {
        if (cache.containsKey(address)) {
            return cache.get(address);
        }
        ZkRegister zkRegister = new ZkRegister(address);
        cache.putIfAbsent(address, zkRegister);
        return cache.get(address);
    }
}
