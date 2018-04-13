/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr;

import java.io.Serializable;

/**
 *
 * @author ws3495
 * @param <T>
 */
public abstract class BaseMsg<T> implements Serializable {
    private MsgType type;
    private T payload;

    public MsgType getType() {
        return type;
    }

    protected void setType(MsgType type) {
        this.type = type;
    }

    public T getPayload() {
        return payload;
    }

    protected void setPayload(T payload) {
        this.payload = payload;
    }
}
