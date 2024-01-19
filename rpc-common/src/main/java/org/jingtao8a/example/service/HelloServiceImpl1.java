package org.jingtao8a.example.service;

import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.example.entity.Hello;
import org.jingtao8a.annotation.RpcService;

@Slf4j
@RpcService(value=HelloService.class, version="1.0")
public class HelloServiceImpl1 implements HelloService {
    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl1.hello");
        String result = "Hello description is " + hello.getDestription();
        return result;
    }
}
