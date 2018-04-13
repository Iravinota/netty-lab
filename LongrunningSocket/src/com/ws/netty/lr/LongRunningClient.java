/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ws3495
 */
public class LongRunningClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 18888;
    private static final int CHANNEL_COUNT = 2;
    
    private EventLoopGroup group;
    private Bootstrap b;
    // channel_id : channel
    private final Map<String, SocketChannel> channelmap = new HashMap<>();
    
    private boolean isClosed = false;
    
    public void init() {
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(60, 30, 0));
                        p.addLast(new ObjectEncoder());
                        p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        p.addLast(new ClientHandler(LongRunningClient.this));       // …Ò∆Ê
                    }
                });
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            doConnect(String.valueOf(i));
        }
    }
    
    String getChannelId(Channel c) {
        Iterator<String> it = channelmap.keySet().iterator();
        while (it.hasNext()) {
            String id = it.next();
            SocketChannel ch = channelmap.get(id);
            if (ch == c) {
                return id;
            }
        }
        return null;
    }
    
    void doConnect(final String id) {
        if (isClosed) {
            System.out.println("Client is ");
        }
        SocketChannel channel = channelmap.get(id);
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture f = b.connect(HOST, PORT);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    SocketChannel c = (SocketChannel)future.channel();
                    channelmap.put(id, c);
                    System.out.println("Channel[" + id + "] connect server success");
                } else {
                    System.out.println("Channel[" + id + "] failed to connect to server, try connect after 10s");
                    future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect(id);
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }
    
    public void sendMsg(String str) {
        CustDevInfo info = new CustDevInfo(str, str);
        PushMsg msg = new PushMsg(info);
        Random r = new Random(System.currentTimeMillis());
        final String id = String.valueOf(r.nextInt(CHANNEL_COUNT));
        SocketChannel channel = channelmap.get(id);
        ChannelFuture f = channel.writeAndFlush(msg);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Send msg successfully using channel[" + id + "]");
                } else {
                    System.out.println("Failed to send msg using channel[" + id + "]");
                }
            }
        });
    }
    
    public void close() {
        group.shutdownGracefully();
        isClosed = true;
    }
    
    public static void main(String[] args) throws InterruptedException {
        LongRunningClient client = new LongRunningClient();
        client.init();
        Scanner s = new Scanner(System.in);
        while (s.hasNextLine()) {
            String str = s.nextLine();
            if (str.equals("exit")) {
                client.close();
                break;
            }
            client.sendMsg(str);
        }
    }
}
