package org.jingtao8a.server.invoke;

import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.dto.RpcRequest;

import java.lang.reflect.Method;

@Slf4j
public class InvokerJDK implements Invoker{
    @Override
    public Object invoke(RpcRequest rpcRequest, Object service) {
        Object res;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            method.setAccessible(true);
            res = method.invoke(service, rpcRequest.getParameters());
            log.info("org.jingtao8a.service:[{}] successful invoke method:[{}]", rpcRequest.getClassName(), rpcRequest.getMethodName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
