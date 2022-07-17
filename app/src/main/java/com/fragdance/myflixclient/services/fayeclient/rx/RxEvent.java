package com.fragdance.myflixclient.services.fayeclient.rx;

import com.fragdance.myflixclient.services.fayeclient.FayeClient;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 15/9/28.
 */
public abstract class RxEvent {

    private final FayeClient mClient;

    public RxEvent(FayeClient client) {
        mClient = client;
    }

    @Override
    public abstract String toString();
}
