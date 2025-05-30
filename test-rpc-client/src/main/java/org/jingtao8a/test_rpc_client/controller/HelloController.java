package org.jingtao8a.test_rpc_client.controller;

import org.jingtao8a.annotation.RpcAutowired;
import org.jingtao8a.client.async.ResponseCallback;
import org.jingtao8a.client.async.RpcContext;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.entity.Hello;
import org.jingtao8a.test_rpc_client.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RpcAutowired(version="1.0")
    private HelloService helloService;

    @RpcAutowired(version="1.0", isAsync = true)
    private  HelloService helloServiceAsync;

    @GetMapping("/hello")
    public String sayHello() {
        String res = helloService.hello(new Hello("111", "222"));
        return res;
    }
    private static class CustomResponseCallback extends ResponseCallback {

        @Override
        public void callBack(RpcResponse result) {
            System.out.println("CustomResponseCalback start");
            System.out.println(result.toString());
            System.out.println("CustomResponseCalback end");
        }

        @Override
        public void onException(RpcResponse result, Exception e) {}
    }
    @GetMapping("/helloAsync")
    public String sayHelloAsync() {
        RpcContext.setCallback(new CustomResponseCallback());
        helloServiceAsync.hello(new Hello("111", "222"));
        return "async call, please see the terminal!";
    }
}
