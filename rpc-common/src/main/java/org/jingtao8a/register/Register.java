package org.jingtao8a.register;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public interface Register {
    default void registerServiceMap(Map<String, Object> serviceMap, InetSocketAddress serverAddress) {
        for (String rpcService : serviceMap.keySet()) {
            registerService(rpcService, serverAddress);
        }
    }
    /* 向注册中心注册服务 */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

    /* 取消所有本机的服务，用于关机的时候 */
    void unregisterAllService(InetSocketAddress inetSocketAddress);

    /* 查找含有某个服务的所有服务端地址 */
    List<String> lookupService(String serviceKey);

    /* 关闭注册中心 */
    void stop();
}
