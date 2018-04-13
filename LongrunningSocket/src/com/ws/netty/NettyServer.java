/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ws3495
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress("22.11.64.27", 9999);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new HelloServerHandler());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println("Server started");
            channelFuture.channel().closeFuture().sync();
            System.out.println("Server finish");
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                bossGroup.shutdownGracefully().sync();
                workGroup.shutdownGracefully().sync();
            } catch (InterruptedException ex) {
                Logger.getLogger(NettyServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static class HelloServerHandler extends SimpleChannelInboundHandler<String> {

        private static long i = 0;
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            i++;
            if (i % 100 == 0) {
                System.out.println("Server recevied " + i + "msgs");
                ctx.channel().close().sync();
            }
//            System.out.println("Server received: " + msg);
//            ctx.writeAndFlush("Server has received you msg");
        }

//        @Override
//        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            System.out.println("Server active");
//            super.channelActive(ctx); //To change body of generated methods, choose Tools | Templates.
//        }
        
        
    }
}
