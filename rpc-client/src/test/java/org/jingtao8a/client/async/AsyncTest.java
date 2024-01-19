package org.jingtao8a.client.async;

import org.jingtao8a.client.core.PendingRpcRequests;
import org.jingtao8a.consts.enums.RpcResponseCodeEnum;
import org.jingtao8a.dto.RpcResponse;
import org.junit.Test;
public class AsyncTest {
    @Test
    public void test1() {
        RpcContext.setCallback(new CustomResponseCallback());
        String requestId = "1234";
        RpcFuture rpcFuture = new RpcFuture();
        rpcFuture.setResponseCallback(RpcContext.getCallback());

        PendingRpcRequests.put(requestId, rpcFuture);
        PendingRpcRequests.complete(new RpcResponse(requestId, RpcResponseCodeEnum.SUCCESS.getCode(), RpcResponseCodeEnum.SUCCESS.getMessage(), "fkdjfkas"));
        try {
            Thread.sleep(2);
            System.out.println("get response" + rpcFuture.get().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static class CustomResponseCallback extends ResponseCallback {

        @Override
        public void callBack(RpcResponse result) {
            System.out.println("callBack");
            System.out.println(result.toString());
        }

        @Override
        public void onException(RpcResponse result, Exception e) {

        }
    }
}
