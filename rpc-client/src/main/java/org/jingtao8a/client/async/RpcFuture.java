package org.jingtao8a.client.async;

import org.jingtao8a.dto.RpcResponse;

import java.util.concurrent.*;

public class RpcFuture implements Future<RpcResponse> {
    private CountDownLatch countDownLatch;
    private RpcResponse rpcResponse;
    private ResponseCallback responseCallback;
    public RpcFuture() {
        countDownLatch = new CountDownLatch(1);
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    /**
     *  阻塞获取结果
     */
    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return rpcResponse;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (countDownLatch.await(timeout, unit)) {
            return rpcResponse;
        }
        return null;
    }

    public void complete(RpcResponse response) {
        this.rpcResponse = response;
        countDownLatch.countDown();
        if (responseCallback != null) {
            responseCallback.success(response);
        }
    }

    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }
}
