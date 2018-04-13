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
public class PingMsg extends BaseMsg {
    public PingMsg() {
        setType(MsgType.PING);
    }
}
