package org.jingtao8a.register;

import org.jingtao8a.register.zk.CuratorUtils;
import org.jingtao8a.register.zk.ZkRegister;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.List;

public class RegisterTest {
    private static Register register = new ZkRegister("127.0.0.1:2181");
    @Test
    public void test() {
        register.registerService("HelloService", new InetSocketAddress("127.0.0.1", 8888));
    }
}
