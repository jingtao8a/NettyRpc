package org.jingtao8.server.invoker;

import org.jingtao8a.config.AllConfig;
import org.jingtao8a.extension.ExtensionLoader;
import org.jingtao8a.server.invoke.Invoker;
import org.junit.Test;

public class InvokerTest {
    @Test
    public void test() {
        Invoker invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension(AllConfig.Invoker);
    }
}
