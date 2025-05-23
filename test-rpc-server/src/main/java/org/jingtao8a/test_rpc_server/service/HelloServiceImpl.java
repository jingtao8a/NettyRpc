package org.jingtao8a.test_rpc_server.service;

import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.annotation.RpcService;
import org.jingtao8a.entity.Hello;
import org.springframework.stereotype.Service;


@Slf4j
@RpcService(value = HelloService.class, version = "1.0")
@Service
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl被创建");
    }
    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
