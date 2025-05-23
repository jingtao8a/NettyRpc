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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
        log.info("Register init");
        return register;
    }

    /**
     * CLIENT
     */
    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "client")
    public NettyClient nettyClient() {
        nettyClient = new NettyClient();
        log.info("NettyClient init");
        return nettyClient;
    }

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "client")
    public FaultTolerantInvoker faultTolerantInvoker() {
        faultTolerantInvoker = ExtensionLoader.getExtensionLoader(FaultTolerantInvoker.class).getExtension(rpcConfig.getFaultTolerantInvoker());
        log.info("FaultTolerantInvoker init");
        return faultTolerantInvoker;
    }

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "client")
    public LoadBalance loadBalance() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(rpcConfig.getLoadBalance());
        log.info("LoadBalance init");
        return loadBalance;
    }

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "client")
    public ProxyFactory proxyFactory() {
        proxyFactory = new ProxyFactory();
        proxyFactory.setNettyClient(nettyClient);
        proxyFactory.setLoadBalance(loadBalance);
        proxyFactory.setRegister(register);
        proxyFactory.setFaultTolerantInvoker(faultTolerantInvoker);
        log.info("ProxyFactory init");
        return proxyFactory;
    }

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "client")
    public ProxyInjectProcessor proxyInjectProcessor() {
        ProxyInjectProcessor proxyInjectProcessor = new ProxyInjectProcessor();
        proxyInjectProcessor.setProxyFactory(proxyFactory);
        log.info("ProxyInjectProcessor init");
        return proxyInjectProcessor;
    }

    /**
     * SERVER
     */

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "server")
    public Invoker invoker() {
        invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension(AllConfig.Invoker);
        log.info("Invoker init");
        return invoker;
    }

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "server")
    public ServiceRegisterCache serviceRegisterCache() {
        serviceRegisterCache = new ServiceRegisterCache();
        log.info("ServiceRegisterCache init");
        return serviceRegisterCache;
    }

    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "server")
    public NettyServer nettyServer() {
        nettyServer = new NettyServer();
        NettyServerThread nettyServerThread = nettyServer.getNettyServerThread();
        nettyServerThread.setRegister(register);
        nettyServerThread.setInvoker(invoker);
        nettyServerThread.setServiceRegisterCache(serviceRegisterCache);
        InetSocketAddress serverAddress = new InetSocketAddress(rpcConfig.getServerIP(), rpcConfig.getServerPort());
        nettyServerThread.setServerAddress(serverAddress);
        log.info("NettyServer init");
        return nettyServer;
    }
    //注册服务
    @Bean
    @ConditionalOnProperty(name = "nettyrpc.type", havingValue = "server")
    public ServiceInjectProcessor serviceInjectProcessor() {
        ServiceInjectProcessor serviceInjectProcessor = new ServiceInjectProcessor();
        serviceInjectProcessor.setServiceRegisterCache(serviceRegisterCache);
        serviceInjectProcessor.setNettyServer(nettyServer);
        log.info("ServiceInjectProcessor init");
        return serviceInjectProcessor;
    }

    @Override
    public void destroy() throws Exception {
        register.stop();
        if (nettyClient != null) {
            nettyClient.stop();
        }
        if (nettyServer != null) {
            nettyServer.stop();
        }
    }
}
