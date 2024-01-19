package org.jingtao8a.server.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NettyServer {
    private NettyServerThread nettyServerThread;
    public NettyServer() {
        nettyServerThread = new NettyServerThread();
    }

    public void start() {
        nettyServerThread.start();
    }

    public void stop() {
        if (nettyServerThread != null && nettyServerThread.isAlive()) {
            nettyServerThread.interrupt();
        }
    }
}
