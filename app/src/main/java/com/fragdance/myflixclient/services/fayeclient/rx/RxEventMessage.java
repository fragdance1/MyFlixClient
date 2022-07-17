package com.fragdance.myflixclient.services.fayeclient.rx;


import com.fragdance.myflixclient.services.fayeclient.FayeClient;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 15/9/29.
 */
public class RxEventMessage extends RxEvent {

    private final Object mMessage;
    private final String mChannel;
    public RxEventMessage(FayeClient client,String channel, Object message) {
        super(client);
        mMessage = message;
        mChannel = channel;
    }

    @SuppressWarnings("unchecke")
    public <T> T message() throws ClassCastException {
        return (T) mMessage;
    }

    @Override
    public String toString() {
        return (String) mMessage;
    }
}
