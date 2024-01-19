package org.jingtao8a.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.client.async.ResponseCallback;
import org.jingtao8a.client.async.RpcContext;
import org.jingtao8a.client.async.RpcFuture;
import org.jingtao8a.consts.RpcConstants;
import org.jingtao8a.consts.enums.RpcErrorMsgEnum;
import org.jingtao8a.consts.enums.RpcResponseCodeEnum;
import org.jingtao8a.dto.RpcMessage;
import org.jingtao8a.dto.RpcRequest;
import org.jingtao8a.dto.RpcResponse;
import org.jingtao8a.exception.RpcException;
import org.jingtao8a.util.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {
    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;
    private final ChannelProvider channelProvider;
    public NettyClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(eventLoopGroup)
                // 连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // (关闭Nagle算法)要求高实时性，有数据发送时就马上发送，就设置为 true 关闭，如果需要减少发送次数减少网络交互，就设置为 false 开启
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientChannelInitializer());
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    public RpcResponse sendRequest(RpcMessage rpcMessage, String targetServiceUrl, boolean isAsync) {
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        // 构造返回Future
        RpcFuture future = new RpcFuture();
        //Channel复用
        Channel channel = getChannel(remoteAddress);
        if (isAsync) {
            return sendAsyncRequest(channel, future, rpcMessage);
        } else {
            return sendSyncRequest(channel, future, rpcMessage);
        }
    }

    private RpcResponse sendAsyncRequest(Channel channel, RpcFuture rpcFuture, RpcMessage rpcMessage) {
        RpcResponse rpcResponse = null;
        String requestId = ((RpcRequest) rpcMessage.getBody()).getRequestId();
        ResponseCallback responseCallback = RpcContext.getCallback();
        rpcFuture.setResponseCallback(responseCallback);
        try {
            PendingRpcRequests.put(requestId, rpcFuture);
            channel.writeAndFlush(rpcMessage).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        log.info("client send message: [{}]", rpcMessage);
                    } else {
                        future.channel().close();
                        log.error("Send failed:", future.cause());
                    }
                }
            });
            // 直接返回空的数据体
            rpcResponse = new RpcResponse(null, RpcResponseCodeEnum.SUCCESS.getCode(), RpcResponseCodeEnum.SUCCESS.getMessage(), null);
        } catch (Exception e) {
            PendingRpcRequests.remove(requestId);
            throw new RpcException(RpcErrorMsgEnum.SERVICE_INVOCATION_FAILURE);
        }
        return rpcResponse;
    }

    private RpcResponse sendSyncRequest(Channel channel, RpcFuture rpcFuture, RpcMessage rpcMessage) {
        RpcResponse rpcResponse = null;
        String requestId = ((RpcRequest) rpcMessage.getBody()).getRequestId();
        try {
            PendingRpcRequests.put(requestId, rpcFuture);
            channel.writeAndFlush(rpcMessage).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        log.info("client send message: [{}]", rpcMessage);
                    } else {
                        future.channel().close();
                        log.error("Send failed:", future.cause());
                    }
                }
            });
            //阻塞等待response
            rpcResponse = rpcFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PendingRpcRequests.remove(requestId);
            throw new RpcException(RpcErrorMsgEnum.SERVICE_INVOCATION_FAILURE);
        }
        return rpcResponse;
    }
    /**
     * 获取和指定地址连接的 channel，Channel复用，不用每次请求都重新连接
     * 如果获取不到，则新建连接（重连）
     *
     * @param inetSocketAddress 待连接scoket地址
     * @return: {@link Channel} 获取到的连接
     */
    @SneakyThrows
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {//没有建立与inetSocketAddress的连接
            //阻塞等待，获取连接成功的channel
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            channel = doConnect(completableFuture, inetSocketAddress, RpcConstants.MAX_RETRY).get();
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 与服务端建立连接
     */
    @SneakyThrows
    public CompletableFuture<Channel> doConnect(CompletableFuture<Channel> completableFuture, InetSocketAddress inetSocketAddress, int retry) {
        log.info("inetSocketAddress [{}]", inetSocketAddress.toString());
        bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                    completableFuture.complete(channelFuture.channel());
                } else if (retry == 0) {
                    log.error("the number of retries expired, connect fail. address:", inetSocketAddress.toString());
                } else {
                    // 当前是第几次重连
                    int now = RpcConstants.MAX_RETRY - retry + 1;
                    // 本次重连的时间间隔
                    int delay = 1 << now;
                    log.warn("connect fail, attempt to reconnect. retry:" + now);
                    bootstrap.config().group().schedule(() ->
                            doConnect(completableFuture, inetSocketAddress, retry - 1), delay, TimeUnit.SECONDS);
                }
            }
        });
        return completableFuture;
    }
    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }
}
