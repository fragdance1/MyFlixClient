package com.fragdance.myflixclient.services

import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.fragdance.myflixclient.Settings
import timber.log.Timber
import java.net.InetAddress

class NetworkDiscoveryService(context:Context) {
    val SERVICE_TYPE = "_myflix._tcp."
    var mServiceName = ""
    val mContext = context
    var nsdManager:NsdManager = (context.getSystemService(Context.NSD_SERVICE)) as NsdManager
    lateinit var discoveryListener:NsdManager.DiscoveryListener
    lateinit var resolveListener:NsdManager.ResolveListener
    lateinit var mService:NsdServiceInfo

    init {
        initializeResolveListener()
        initializeDiscoveryListener();
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    private fun initializeResolveListener() {
        resolveListener = object : NsdManager.ResolveListener {

            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Called when the resolve fails. Use the error code to debug.
                Timber.tag(Settings.TAG).d("Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Timber.tag(Settings.TAG).d("Resolve Succeeded. $serviceInfo")

                if (serviceInfo.serviceName === mServiceName) {
                    return
                }
                mService = serviceInfo
               Timber.tag(Settings.TAG).d("serviceInfo "+serviceInfo.host.hostAddress)
                //Settings.SERVER = "http://"+serviceInfo.host.hostAddress + ":8000"//+serviceInfo.port

                val intent = Intent()
                intent.action = "server_found"
                intent.putExtra("server",serviceInfo.host.hostAddress)
                intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                mContext.sendBroadcast(intent)

//                Timber.tag(Settings.TAG).d("Server: "+Settings.SERVER)
//                val port: Int = serviceInfo.port
//                val host: InetAddress = serviceInfo.host
            }
        }
    }

    private fun initializeDiscoveryListener() {
        discoveryListener = object : NsdManager.DiscoveryListener {

            override fun onDiscoveryStarted(regType: String) {}

            override fun onServiceFound(service: NsdServiceInfo) {
                if(service.serviceType == SERVICE_TYPE && service.serviceName != mServiceName) {
                    Timber.tag(Settings.TAG).d("Service discovery success $service")
                    mServiceName = service.serviceName
                    nsdManager.resolveService(service,resolveListener)
                }

            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Timber.tag(Settings.TAG).d("service lost: $service")

            }

            override fun onDiscoveryStopped(serviceType: String) {
                Timber.tag(Settings.TAG).d( "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Timber.tag(Settings.TAG).d("Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Timber.tag(Settings.TAG).d( "Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
            }
        }
    }
}



