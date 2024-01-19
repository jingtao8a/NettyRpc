package org.jingtao8a.async;

public class RpcContext {
    private static ThreadLocal<ResponseCallback> callback = new ThreadLocal<>();

    public static void setCallback(ResponseCallback responseCallback) {
        callback.set(responseCallback);
    }

    public static ResponseCallback getCallback() {
        return callback.get();
    }
}
