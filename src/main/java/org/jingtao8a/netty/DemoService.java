package org.jingtao8a.netty;

public class DemoService {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start: 开始整个Netty搭建流程");
        server s = new server();
        client c = new client();
        s.start();
        c.start();
    }
}
