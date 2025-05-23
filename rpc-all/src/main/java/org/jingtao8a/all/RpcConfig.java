package org.jingtao8a.all;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="nettyrpc")
public class RpcConfig {
    //服务端还是客户端
    public String type = "server";
    //SPI 选项
    public String faultTolerantInvoker = "fail-fast";
    public String loadBalance = "random";
    public String registerFactory="zookeeper";
    public String serializer = "kryo";
    public String invoker = "jdk";

    //Client
    private Integer retryTimes = 3;

    //Server
    public String serverIP = "127.0.0.1";//server端地址
    public Integer serverPort = 6699; //开放端口

    //注册中心
    public String registerAddress="127.0.0.1:2181";
}
