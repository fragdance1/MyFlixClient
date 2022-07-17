package com.fragdance.myflixclient.services.fayeclient.rx;


import com.fragdance.myflixclient.services.fayeclient.FayeClient;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 15/9/29.
 */
public class RxEventDisconnected extends RxEvent {

    public RxEventDisconnected(FayeClient client) {
        super(client);
    }

    @Override
    public String toString() {
        return "Faye client is disconnected to server";
    }
}
