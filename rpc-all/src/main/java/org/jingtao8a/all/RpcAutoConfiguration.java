package org.jingtao8a.all;

import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.annotation.RpcService;
import org.jingtao8a.client.core.NettyClient;
import org.jingtao8a.client.faultTolerantInvoker.FaultTolerantInvoker;
import org.jingtao8a.client.loadbalance.LoadBalance;
import org.jingtao8a.client.proxy.ProxyFactory;
import org.jingtao8a.config.AllConfig;
import org.jingtao8a.example.service.HelloServiceImpl1;
import org.jingtao8a.extension.ExtensionLoader;
import org.jingtao8a.register.Register;
import org.jingtao8a.register.RegisterFactory;
import org.jingtao8a.server.core.NettyServer;
import org.jingtao8a.server.core.NettyServerThread;
import org.jingtao8a.server.core.ServiceRegisterCache;
import org.jingtao8a.server.invoke.Invoker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Slf4j
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcAutoConfiguration implements DisposableBean {
    @Resource
    private RpcConfig rpcConfig;
    // client
    private NettyClient nettyClient;
    private FaultTolerantInvoker faultTolerantInvoker;
    private LoadBalance loadBalance;
    private ProxyFactory proxyFactory;

    //common
    private Register register;

    //server
    private NettyServer nettyServer;
    private Invoker invoker;
    private ServiceRegisterCache serviceRegisterCache;

    /**
     * COMMON
     */
    @Bean
    public Register serviceDiscovery() {
        RegisterFactory registerFactory = ExtensionLoader.getExtensionLoader(RegisterFactory.class).getExtension(rpcConfig.getRegisterFactory());
        register = registerFactory.getRegister(rpcConfig.getRegisterAddress());
        return register;
    }

    /**
     * CLIENT
     */
    @Bean
    public NettyClient nettyClient() {
        nettyClient = new NettyClient();
        return nettyClient;
    }

    @Bean
    public FaultTolerantInvoker faultTolerantInvoker() {
        faultTolerantInvoker = ExtensionLoader.getExtensionLoader(FaultTolerantInvoker.class).getExtension(rpcConfig.getFaultTolerantInvoker());
        return faultTolerantInvoker;
    }

    @Bean
    public LoadBalance loadBalance() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(rpcConfig.getLoadBalance());
        return loadBalance;
    }

    @Bean
    public ProxyFactory proxyFactory() {
        proxyFactory = new ProxyFactory();
        proxyFactory.setNettyClient(nettyClient);
        proxyFactory.setLoadBalance(loadBalance);
        proxyFactory.setRegister(register);
        proxyFactory.setFaultTolerantInvoker(faultTolerantInvoker);
        return proxyFactory;
    }

    @Bean
    public ProxyInjectProcessor proxyInjectProcessor() {
        ProxyInjectProcessor proxyInjectProcessor = new ProxyInjectProcessor();
        proxyInjectProcessor.setProxyFactory(proxyFactory);
        return proxyInjectProcessor;
    }

    /**
     * SERVER
     */
    @Bean
    public NettyServer nettyServer() {
        nettyServer = new NettyServer();
        NettyServerThread nettyServerThread = nettyServer.getNettyServerThread();
        nettyServerThread.setRegister(register);
        nettyServerThread.setInvoker(invoker);
        nettyServerThread.setServiceRegisterCache(serviceRegisterCache);
        InetSocketAddress serverAddress = new InetSocketAddress(rpcConfig.getServerIP(), rpcConfig.getServerPort());
        nettyServerThread.setServerAddress(serverAddress);
        return nettyServer;
    }

    @Bean
    public Invoker invoker() {
        invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension(AllConfig.Invoker);
        return invoker;
    }

    @Bean
    public ServiceRegisterCache serviceRegisterCache() {
        serviceRegisterCache = new ServiceRegisterCache();
        return serviceRegisterCache;
    }

    //注册服务
    @Bean
    public ServiceInjectProcessor serviceInjectProcessor() {
        ServiceInjectProcessor serviceInjectProcessor = new ServiceInjectProcessor();
        serviceInjectProcessor.setServiceRegisterCache(serviceRegisterCache);
        serviceInjectProcessor.setNettyServer(nettyServer);
        return serviceInjectProcessor;
    }

    @Override
    public void destroy() throws Exception {
        register.stop();
        nettyClient.stop();
        nettyServer.stop();
    }
}
