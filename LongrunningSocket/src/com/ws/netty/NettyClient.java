/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Netty的input和output就是一个一个的handler串联起来的
 * @author ws3495
 */
public class NettyClient {
    
    private static EventLoopGroup group = null;
    private static Channel ch = null;
    
    private static void readCommandToMoveNext(Scanner scanner, String info) {
        System.out.println(info + ", please input command to move next: ");
        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            break;
        }
    }
    
    public static void main(String[] args) {
        long i = 0;
        Scanner scanner = new Scanner(System.in);
        try {
            readCommandToMoveNext(scanner, "Before init channel");
            initChannel2();
        } catch (InterruptedException ex) {
            Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            if (i == 90) {
                readCommandToMoveNext(scanner, "Before server close channel");
            }
            sendMsg2();
            i++;
            if (i % 100 == 0) {
                System.out.println("Send " + i + " msgs");
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void initChannel1() throws InterruptedException {
        if (ch == null || !ch.isActive()) {
            if (ch != null) {
                ch.close().sync();
                System.out.println("Close Channel Before Init");
            }
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress("22.11.64.27", 9999)
                    .handler(new ChannelInitializer<SocketChannel>() {      // 初始化一个初始的handler
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new HelloClientHandler());
                }
            });
            ch = clientBootstrap.connect().sync().channel();
        }
    }
    
    private static void initChannel2() throws InterruptedException {
        if (group == null || group.isShutdown() || group.isTerminated() || ch == null || !ch.isActive()) {
//            if (ch != null) {
//                ch.close().sync();
//                System.out.println("Close Channel-2 Before Init");
//            }
            if (group != null) {
                group.shutdownGracefully().sync();
                System.out.println("Close Group Before Init");
            }
            group = new NioEventLoopGroup();
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("22.11.64.27", 9999)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new HelloClientHandler());
                }
            });
            ch = clientBootstrap.connect().sync().channel();
        }
    }
    
    private static void sendMsg1() {
        try {
            initChannel1();
            ch.writeAndFlush("This is a bad boy\r\n");
//            ch.close().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex);
            try {
                System.out.println("Close Channel");
                ch.close().sync();
            } catch (InterruptedException ex1) {
                Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    private static void sendMsg2() {
        try {
            initChannel2();
            ch.writeAndFlush("This is a bad boy\r\n");
        } catch (Exception ex) {
            Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex);
            try {
                System.out.println("发生了一个未知错误，关闭Group，等待重新建立");
                group.shutdownGracefully().sync();
            } catch (InterruptedException ex1) {
                Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    private static void sendMsg0() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 9999)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new HelloClientHandler());
                }
            });
            ch = clientBootstrap.connect().sync().channel();
            ch.writeAndFlush("This is a good boy\r\n");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException ex) {
                Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static class HelloClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("Client received: " + msg);
        }

//        @Override
//        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//            System.out.println("Client close ");
//            super.channelInactive(ctx); //To change body of generated methods, choose Tools | Templates.
//        }

//        @Override
//        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            System.out.println("Client active ");
//            super.channelActive(ctx); //To change body of generated methods, choose Tools | Templates.
//        }
        
        
        
    }
}
