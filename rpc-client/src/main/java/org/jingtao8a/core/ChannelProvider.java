package org.jingtao8a.core;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Channel复用
 */
public class ChannelProvider {
    private final Map<InetSocketAddress, Channel> channelMap = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress address) {
        if (channelMap.containsKey(address)) {
            Channel channel = channelMap.get(address);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(address);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        channelMap.put(inetSocketAddress, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        channelMap.remove(inetSocketAddress);
    }
}
