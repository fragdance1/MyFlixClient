package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.services.fayeclient.FayeClient
import com.fragdance.myflixclient.services.fayeclient.FayeClientListener
import com.fragdance.myflixclient.services.fayeclient.MetaMessage
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import org.cometd.bayeux.Channel
import org.cometd.bayeux.Message
import org.cometd.bayeux.client.ClientSession
import org.cometd.bayeux.client.ClientSessionChannel
import org.cometd.client.BayeuxClient
import org.cometd.client.http.okhttp.OkHttpClientTransport
import org.cometd.client.transport.ClientTransport
import org.cometd.client.transport.ClientTransport.MAX_NETWORK_DELAY_OPTION
import org.cometd.client.websocket.common.AbstractWebSocketTransport.IDLE_TIMEOUT_OPTION
import org.cometd.client.websocket.okhttp.OkHttpWebSocketTransport
import org.cometd.common.JacksonJSONContextClient
import timber.log.Timber
import java.util.concurrent.TimeUnit


class FayeService: FayeClientListener {
    var  client:FayeClient
    var callbacks = HashMap<String,(message:String?)->Unit>();
    init {
        var meta = MetaMessage();
        client = FayeClient("ws://192.168.1.121:8000/api/faye/",meta);
        client.setListener( this);
        client.connectServer();
    }

    private fun subscribe(channel:String, callback: (message:String?)->Unit):Boolean {
    if(client.isConnectedServer) {
        client.subscribeChannel(channel);
        callbacks[channel] = callback
        return true
    } else {
        return false
    }

    }

    private fun unsubscribe(channel:String) {
        if(client.isConnectedServer) {
            client.unsubscribeChannel(channel)
        }
         callbacks.remove(channel)
    }
    companion object {
        private var mInstance:FayeService? = null

        fun create() {
            if(mInstance == null) {
                mInstance = FayeService()
            }
        }
        fun subscribe(channel:String,callback:(message:String?)->Unit):Boolean {
            if(mInstance == null) {
                mInstance = FayeService()
            }
            return mInstance?.subscribe(channel,callback)?:false;
        }

        fun unsubscribe(channel:String) {
            if(mInstance == null) {
                mInstance = FayeService()
            }
            mInstance?.unsubscribe(channel)
        }
    }

    override fun onConnectedServer(fc: FayeClient?) {
        Timber.tag(Settings.TAG).d("Connected to server")
    }

    override fun onDisconnectedServer(fc: FayeClient?) {
        Timber.tag(Settings.TAG).d("Disconnected from server")
    }

    override fun onReceivedMessage(fc: FayeClient?, channel:String,msg: String?) {
        callbacks[channel]?.let { it(msg) }

    }
}