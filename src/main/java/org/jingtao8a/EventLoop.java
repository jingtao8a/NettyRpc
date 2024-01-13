package org.jingtao8a;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoop {
    public static void main(String[] args) {
        //1. 创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(); // io 事件，普通任务，定时任务
//        执行普通任务
//        group.next().submit(()->{
//           try {
//               Thread.sleep(1000);
//           } catch (InterruptedException e) {
//               e.printStackTrace();
//           }
//           log.debug("ok");
//        });
//      执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);
        log.debug("main");
    }
}
