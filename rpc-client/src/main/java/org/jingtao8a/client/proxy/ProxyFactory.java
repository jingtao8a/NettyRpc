package org.jingtao8a.client.proxy;

import org.jingtao8a.client.core.NettyClient;
import org.jingtao8a.client.faultTolerantInvoker.FaultTolerantInvoker;
import org.jingtao8a.client.loadbalance.LoadBalance;
import org.jingtao8a.consts.RpcConstants;
import org.jingtao8a.consts.enums.MessageTypeEnum;
import org.jingtao8a.consts.enums.RpcErrorMsgEnum;
import org.jingtao8a.consts.enums.RpcResponseCodeEnum;
import org.jingtao8a.consts.enums.SerializerTypeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.exception.RpcException;
import org.jingtao8a.register.Register;
import org.jingtao8a.util.ServiceUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProxyFactory {
    private Register register;
    private NettyClient nettyClient;
    private LoadBalance loadBalance;
    private FaultTolerantInvoker faultTolerantInvoker;
    private String serializer;
    private Map<String, Object> objectCache = new HashMap<>();
    private Map<String, Object> asyncObjectCache = new HashMap<>();

    public <T> T getProxy(Class<T> interfaceClass, String version, boolean isAsync) {
        if (isAsync) { // 异步
            return (T) asyncObjectCache.computeIfAbsent(interfaceClass.getName() + version, clz->{
                return Proxy.newProxyInstance(
                        interfaceClass.getClassLoader(),
                        new Class<?>[]{interfaceClass},
                        new ObjectProxy<T>(interfaceClass, version, isAsync));
            });
        } else { // 同步
            return (T) objectCache.computeIfAbsent(interfaceClass.getName() + version, clz->{
                return Proxy.newProxyInstance(
                        interfaceClass.getClassLoader(),
                        new Class<?>[]{interfaceClass},
                        new ObjectProxy<T>(interfaceClass, version, isAsync));
            });
        }
    }

    private class ObjectProxy<T> implements InvocationHandler {
        private Class<T> clazz;
        private String version;
        private boolean isAsync;
        public ObjectProxy(Class<T> clazz, String version, boolean isAsync) {
            this.clazz = clazz;
            this.version = version;
            this.isAsync = isAsync;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                    method.getName(), method.getParameterTypes(), args, version);
            String rpcServiceName = rpcRequest.getClassName();
            String version = rpcRequest.getVersion();
            String serviceKey = ServiceUtil.makeServiceKey(rpcServiceName, version);
            //从注册中心拿到该rpcService下的所有server的Address
            List<String> serviceUrlList = register.lookupService(serviceKey);
            //负载均衡
            String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
            //封装Message
            RpcMessage rpcMessage = new RpcMessage(MessageTypeEnum.REQUEST_TYPE, SerializerTypeEnum.getCode(serializer),
                    (byte) 0, RpcConstants.REQUEST_ID.getAndIncrement(), rpcRequest);
            RpcResponse rpcResponse = null;
            rpcResponse = faultTolerantInvoker.doInvoke(nettyClient, rpcMessage, targetServiceUrl, isAsync);
            this.check(rpcResponse, rpcRequest);
            return rpcResponse.getData();
        }

        private void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
            if (rpcResponse == null) {
                throw new RpcException(RpcErrorMsgEnum.SERVICE_INVOCATION_FAILURE);
            }
            if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
                throw new RpcException(RpcErrorMsgEnum.REQUEST_NOT_MATCH_RESPONSE);
            }
            if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
                throw new RpcException(RpcErrorMsgEnum.SERVICE_INVOCATION_FAILURE);
            }
        }
    }
}
