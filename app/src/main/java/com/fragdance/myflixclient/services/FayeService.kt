package com.fragdance.myflixclient.services

import android.widget.Toast
import com.fragdance.myflixclient.MainActivity
import com.fragdance.myflixclient.MyFlixApplication
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

    var client = BayeuxClient("http://192.168.1.80:5001/api/faye",wsTransport,httpTransport)

    init {
        client.handshake(this)
    }

    public fun subscribe(channel:String, callback: (message:Message?)->Unit):Boolean {
        val c = client.getChannel(channel)
        val listener = ClientSessionChannel.MessageListener{channel,message -> run {callback(message)}}
        return c.subscribe(listener)
    }


    override fun onMessage(message: Message?) {
        Timber.tag(Settings.TAG).d("message "+message)
/*
        var channel:ClientSessionChannel = client.getChannel("/test/toaster");
        Timber.tag(Settings.TAG).d("channel "+channel)
        var listener =
            ClientSessionChannel.MessageListener { channel, message -> run {
                Timber.tag(Settings.TAG).d("Got message "+message)

                val context = MyFlixApplication.instance.applicationContext
                val toast = Toast.makeText(MainActivity.mInstance,"Testar hallo",Toast.LENGTH_LONG)
                toast.show()


            }
            }

        // Send the subscription to the server.
        if(channel.subscribe(
            listener
        )) {
            Timber.tag(Settings.TAG).d("Sent")
        }
        */

    }
}