package org.jingtao8a.consts;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcConstants {
    public static byte[] MAGIC_NUMBER = {(byte) '2', (byte)'0', (byte)'2', (byte)'4'};
    public static byte VERSION = 1;

    /* 请求ID RequestId */
    public static AtomicInteger REQUEST_ID = new AtomicInteger(0);

    public static int MAGIC_LENGTH = 4;
    public static int VERSION_LENGTH = 1;
    public static int FULL_LENGTH_LENGTH = 4;
    public static int HEAD_LENGTH = 16;
    public static int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    public static String PING = "ping";
    public static String PONG = "pong";
    /**
     *  客户端重连次数
     */
    public static int MAX_RETRY = 5;
}
