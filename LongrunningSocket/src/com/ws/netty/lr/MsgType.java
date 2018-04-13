/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr;

/**
 *
 * @author ws3495
 */
public enum MsgType {
    /**
     * 长连接探测消息
     */
    PING,
    /**
     * 长连接探测返回消息
     */
    PONG,
    /**
     * 消息推送消息
     */
    PUSHMSG
}
