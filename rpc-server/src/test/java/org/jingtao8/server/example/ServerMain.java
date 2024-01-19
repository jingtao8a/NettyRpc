package org.jingtao8.server.example;

import org.jingtao8a.example.service.HelloServiceImpl1;
import org.jingtao8a.annotation.RpcService;
import org.jingtao8a.config.AllConfig;
import org.jingtao8a.extension.ExtensionLoader;
import org.jingtao8a.register.Register;
import org.jingtao8a.register.RegisterFactory;
import org.jingtao8a.server.core.NettyServer;
import org.jingtao8a.server.core.NettyServerThread;
import org.jingtao8a.server.core.ServiceRegisterCache;
import org.jingtao8a.server.invoke.Invoker;

import java.net.InetSocketAddress;

public class ServerMain {
    private NettyServer nettyServer;

    private Register register;
    private Invoker invoker;
    private ServiceRegisterCache serviceRegisterCache;
    private InetSocketAddress serverAddress;


    private ServerMain() {
        nettyServer = new NettyServer();
        RegisterFactory registerFactory = ExtensionLoader.getExtensionLoader(RegisterFactory.class).getExtension(AllConfig.RegisterFactory);
        register = registerFactory.getRegister(AllConfig.registerAddress);
        invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension(AllConfig.Invoker);
        serviceRegisterCache = new ServiceRegisterCache();
        serverAddress = new InetSocketAddress(AllConfig.serverIP, AllConfig.serverPort);
        configNettyThread();
        configSerice();
    }
    void configNettyThread() {
        NettyServerThread nettyServerThread = nettyServer.getNettyServerThread();
        nettyServerThread.setRegister(register);
        nettyServerThread.setInvoker(invoker);
        nettyServerThread.setServiceRegisterCache(serviceRegisterCache);
        nettyServerThread.setServerAddress(serverAddress);
    }
    void configSerice() {
        Class<?> clazz = HelloServiceImpl1.class;
        RpcService rpcService = clazz.getAnnotation(RpcService.class);
        HelloServiceImpl1 serviceBean = new HelloServiceImpl1();
        serviceRegisterCache.addService(rpcService.value().getName(), rpcService.version(), serviceBean);
    }

    void start() {
        nettyServer.start();
    }
    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain();
        serverMain.start();
    }
}
