/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * @author ws3495
 */
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//        byte[] lenbuf = new byte[LongRunningServer.LENGTH_LEN];
//        msg.readBytes(lenbuf);
//        System.out.println("len=" + Publib.bytesToHex(lenbuf));
        
        int len = msg.readableBytes();
        byte[] data = new byte[len];
        msg.readBytes(data);
        if (len == 4) {
            String str = new String(data);
            if (str.equals("ping")) {
                System.out.println("Server receive ping msg");
                ctx.writeAndFlush("pong".getBytes());
                System.out.println("Server send pong msg");
                return;
            }
        }
        System.out.println("Server receive msg: " + new String(data));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.err);
        ctx.close();
    }
    
}
