/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ws.netty.lr;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ws3495
 */
public class Clients {
    private final int number;
    private List<LongRunningClient> clients = new ArrayList<>();

    public Clients(int number) {
        this.number = number;
    }
    
    private void init() {
        for (int i = 0; i < number; i++) {
            LongRunningClient client = new LongRunningClient();
            client.init();
            clients.add(client);
        }
    }
    
    public void close() {
        for (LongRunningClient client : clients) {
            client.close();
            System.out.println("Close client");
        }
    }
    
    
}
