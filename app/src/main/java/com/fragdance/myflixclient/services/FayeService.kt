package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import okhttp3.OkHttpClient
import org.cometd.bayeux.Message
import org.cometd.bayeux.client.ClientSession
import org.cometd.bayeux.client.ClientSessionChannel
import org.cometd.client.BayeuxClient
import org.cometd.client.http.okhttp.OkHttpClientTransport
import org.cometd.client.transport.ClientTransport
import org.cometd.client.websocket.okhttp.OkHttpWebSocketTransport
import org.cometd.common.JacksonJSONContextClient
import timber.log.Timber


class FayeService: ClientSession.MessageListener {
    var httpClient = OkHttpClient()

    var transportOptions = HashMap<String,JacksonJSONContextClient>()
    var jsonContext = JacksonJSONContextClient()
    init {
        transportOptions.put(ClientTransport.JSON_CONTEXT_OPTION, jsonContext);
    }

    var wsTransport = OkHttpWebSocketTransport(transportOptions as Map<String, Any>?,httpClient)
    var httpTransport = OkHttpClientTransport(transportOptions as Map<String, Any>?,httpClient)

    var client = BayeuxClient(Settings.SERVER+"/api/faye",wsTransport,httpTransport)

    init {
        client.handshake(this)
    }

    private fun subscribe(channel:String, callback: (message:Message?)->Unit):Boolean {
        val c = client.getChannel(channel)
        val listener = ClientSessionChannel.MessageListener{channel,message -> run {callback(message)}}
        return c.subscribe(listener)
    }

    override fun onMessage(message: Message?) {
        //Timber.tag(Settings.TAG).d("message "+message)
    }

    companion object {
        private var mInstance:FayeService? = null

        fun create() {
            if(mInstance == null) {
                mInstance = FayeService()
            }
        }
        fun subscribe(channel:String,callback:(message:Message?)->Unit):Boolean {
            if(mInstance == null) {
                mInstance = FayeService()
            }
            return mInstance?.subscribe(channel,callback)?:false

        }
    }
}