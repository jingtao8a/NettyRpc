package org.jingtao8a.core;

import org.jingtao8a.async.RpcFuture;
import org.jingtao8a.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PendingRpcRequests {
    /**
     *  requestId : RpcFuture(RpcResponse)
     */
    public static final Map<String, RpcFuture> PENDING_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    private PendingRpcRequests() {}
    public static void put(String requestId, RpcFuture future) {
        PENDING_RESPONSE_FUTURES.put(requestId, future);
    }

    public static  void remove(String requestId) {
        PENDING_RESPONSE_FUTURES.remove(requestId);
    }

    public static void complete(RpcResponse rpcResponse) {
        RpcFuture future = PENDING_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
