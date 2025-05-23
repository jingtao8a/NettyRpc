package org.jingtao8a.all;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="nettyrpc")
public class RpcConfig {
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
    public Integer serverPort = 9988; //开放端口

    //注册中心
    public String registerAddress="127.0.0.1:2181";
}
