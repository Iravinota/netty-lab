/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 *
 * @author ws3495
 */
public class ServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg msg) throws Exception {
        switch (msg.getType()) {
            case PING:
                System.out.println("Server receive PingMsg");
                ctx.writeAndFlush(new PongMsg());
                System.out.println("Server send PongMsg");
                break;
                
            case PUSHMSG:
                CustDevInfo info = (CustDevInfo)msg.getPayload();
                System.out.println(info);
                break;
                
            default:
                System.out.println("Server receive msg: " + msg);
        }
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.err);
        ctx.close();
    }
    
    
    
}
