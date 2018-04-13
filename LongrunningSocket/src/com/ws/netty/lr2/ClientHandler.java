/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *
 * @author ws3495
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    // 是否接收到pong。会出现线程问题吗？
    private boolean isReceivePong = true;
    private final LongRunningClient client;

    public ClientHandler(LongRunningClient client) {
        this.client = client;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent)evt;
            if (e.state() == IdleState.READER_IDLE) {           // 长时间没有接收到数据，探测是否连接还正常
                if (isReceivePong) {
                    ctx.writeAndFlush("ping".getBytes());
                    System.out.println("Channel[" + client.getChannelId(ctx.channel()) + "] send ping msg to server");
                    isReceivePong = false;
                } else {
                    // 若发送ping后长时间接收不到pong，则断开连接
                    ctx.close();
                    System.out.println("Close ctx when not receiving pong msg after a long time");
                }
            } else if (e.state() == IdleState.WRITER_IDLE) {    // 长时间没有发送数据
                ctx.writeAndFlush("ping".getBytes());
                isReceivePong = false;
                System.out.println("Channel[" + client.getChannelId(ctx.channel()) + "] send ping msg to server");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.err);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel[" + client.getChannelId(ctx.channel()) + "] closed, will reconnect");
        String id = client.getChannelId(ctx.channel());
        if (id == null) {
            System.err.println("Can't find this channel in channel map. Close this channel");
        } else {
            client.doConnect(id);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int len = msg.readableBytes();
        byte[] data = new byte[len];
        msg.readBytes(data);
        if (len == 4) {
            String str = new String(data);
            if (str.equals("pong")) {
                isReceivePong = true;
                System.out.println("Channel[" + client.getChannelId(ctx.channel()) + "] receive pong msg from server");
                return;
            }
        }
        
        System.out.println("Channel[" + client.getChannelId(ctx.channel()) + "] receive msg from server: " + new String(data));
    }
    
    
    
}
