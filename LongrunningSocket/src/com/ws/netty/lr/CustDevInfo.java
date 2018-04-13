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
 */
public class CustDevInfo implements Serializable {
    private final String custno;
    private final String devurl;

    public CustDevInfo(String custno, String devurl) {
        this.custno = custno;
        this.devurl = devurl;
    }

    public String getCustno() {
        return custno;
    }

    public String getDevurl() {
        return devurl;
    }

    @Override
    public String toString() {
        return "CustDevInfo{" + "custno=" + custno + ", devurl=" + devurl + '}';
    }
    
}
