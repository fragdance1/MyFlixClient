package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import okhttp3.OkHttpClient
import org.cometd.client.BayeuxClient
import org.cometd.client.transport.ClientTransport

import org.cometd.client.websocket.okhttp.OkHttpWebSocketTransport

class FayeService {
    companion object {
        private var client:BayeuxClient? = null

        fun get(): BayeuxClient {
            if(FayeService.client == null) {

/*
                // Configure BayeuxClient, with the websocket transport listed before the long-polling transport.
                FayeService.client = BayeuxClient(Settings.SERVER+"/faye", OkHttpWebSocketTransport())
                client?.handshake();
                client?.waitFor(1000, BayeuxClient.State.CONNECTED);
*/
            }
            return client!!
        }
    }
}