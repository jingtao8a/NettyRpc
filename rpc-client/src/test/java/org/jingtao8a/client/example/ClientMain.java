package org.jingtao8a.client.example;

import com.sun.security.ntlm.Client;
import org.jingtao8a.client.core.NettyClient;
import org.jingtao8a.client.faultTolerantInvoker.FaultTolerantInvoker;
import org.jingtao8a.client.loadbalance.LoadBalance;
import org.jingtao8a.client.proxy.ProxyFactory;
import org.jingtao8a.config.AllConfig;
import org.jingtao8a.example.entity.Hello;
import org.jingtao8a.example.service.HelloService;
import org.jingtao8a.extension.ExtensionLoader;
import org.jingtao8a.register.Register;
import org.jingtao8a.register.RegisterFactory;


public class ClientMain {
    private Register register;
    private NettyClient nettyClient;
    private ProxyFactory proxyFactory;
    private FaultTolerantInvoker faultTolerantInvoker;
    private LoadBalance loadBalance;

    private ClientMain() {
        RegisterFactory registerFactory = ExtensionLoader.getExtensionLoader(RegisterFactory.class).getExtension(AllConfig.RegisterFactory);
        register = registerFactory.getRegister(AllConfig.registerAddress);
        nettyClient = new NettyClient();
        proxyFactory = new ProxyFactory();
        faultTolerantInvoker = ExtensionLoader.getExtensionLoader(FaultTolerantInvoker.class).getExtension(AllConfig.FaultTolerantInvoker);
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(AllConfig.LoadBalance);
        proxyFactory.setNettyClient(nettyClient);
        proxyFactory.setRegister(register);
        proxyFactory.setLoadBalance(loadBalance);
        proxyFactory.setFaultTolerantInvoker(faultTolerantInvoker);
    }

    public <T> T getProxy(Class<T> interfaceClass, String version, boolean isAsync) {
        return proxyFactory.getProxy(interfaceClass, version, isAsync);
    }
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();

        HelloService helloService = clientMain.getProxy(HelloService.class, "1.0", false);

        String res = helloService.hello(new Hello("yxt", "jingtao8a"));

        System.out.println(res);
    }
}
