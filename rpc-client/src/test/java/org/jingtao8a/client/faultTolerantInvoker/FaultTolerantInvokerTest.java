package org.jingtao8a.client.faultTolerantInvoker;


import org.jingtao8a.extension.ExtensionLoader;
import org.junit.Test;


public class FaultTolerantInvokerTest {
    @Test
    public void test1() {
        FaultTolerantInvoker faultTolerantInvoker = ExtensionLoader.getExtensionLoader(FaultTolerantInvoker.class).getExtension("retry");
        System.out.println(faultTolerantInvoker.getClass().getName());
    }
}
