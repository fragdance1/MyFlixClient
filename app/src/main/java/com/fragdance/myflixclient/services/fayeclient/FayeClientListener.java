package com.fragdance.myflixclient.services.fayeclient;

/**
 * @author Sheng-Yuan Wang (2015/9/7).
 */
public interface FayeClientListener {

    void onConnectedServer(FayeClient fc);
    void onDisconnectedServer(FayeClient fc);
    void onReceivedMessage(FayeClient fc, String channel, String msg);

}
