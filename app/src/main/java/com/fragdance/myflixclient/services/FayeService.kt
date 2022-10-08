package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.services.fayeclient.FayeClient
import com.fragdance.myflixclient.services.fayeclient.FayeClientListener
import com.fragdance.myflixclient.services.fayeclient.MetaMessage
import timber.log.Timber

class FayeService: FayeClientListener {
    var  client:FayeClient
    var callbacks = HashMap<String,(message:String?)->Unit>();
    init {
        var meta = MetaMessage();
        client = FayeClient("ws://192.168.1.79:8000/api/faye/",meta);
        client.listener = this;
        client.connectServer();
    }

    private fun subscribe(channel:String, callback: (message:String?)->Unit):Boolean {
    if(client.isConnectedServer) {
        client.subscribeChannel(channel);
        callbacks[channel] = callback
        return true
    } else {
        client.connectServer()
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
        Timber.tag(Settings.TAG).d("Faye Connected to server")
        FayeService.subscribe("/test/test") { message ->
            run {
                Timber.tag(Settings.TAG).d("Test")
            }
        }
    }

    override fun onDisconnectedServer(fc: FayeClient?) {
        Timber.tag(Settings.TAG).d("Faye Disconnected from server")
    }

    override fun onReceivedMessage(fc: FayeClient?, channel:String,msg: String?) {
        Timber.tag(Settings.TAG).d("FAYE received message "+channel);
        callbacks[channel]?.let { it(msg) }

    }
}