package org.jingtao8a.server.core;

import lombok.Getter;
import org.jingtao8a.consts.enums.RpcErrorMsgEnum;
import org.jingtao8a.exception.RpcException;
import org.jingtao8a.util.ServiceUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务端缓存
 */
@Getter
public class ServiceRegisterCache {
    /**
     * 服务缓存
     */
    private final Map<String, Object> serviceMap;

    public ServiceRegisterCache() {
        serviceMap = new ConcurrentHashMap<>();
    }


    public void addService(String interfaceName, String version, Object serviceBean) {
        String serviceKey = ServiceUtil.makeServiceKey(interfaceName, version);
        serviceMap.put(serviceKey, serviceBean);
    }

    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMsgEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
