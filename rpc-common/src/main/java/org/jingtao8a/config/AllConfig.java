package org.jingtao8a.config;

public class AllConfig {
    //SPI 选项
    public static String FaultTolerantInvoker = "fail-fast";
    public static String LoadBalance = "random";
    public static String RegisterFactory="zookeeper";
    public static String Serializer = "kryo";
    public static String Invoker = "jdk";

    //Client


    //Server
    public static String serverIP = "127.0.0.1";//server端地址
    public static int serverPort = 9988; //开放端口

    //注册中心
    public static String registerAddress="127.0.0.1:2181";
}
