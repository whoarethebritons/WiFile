package com.example.wifile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.IBinder;
import android.util.Log;

import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by Kait on 9/29/2014.
 */
public class NsdHelper extends Server {

    Context wfContext;

    NsdManager wfNsdManager;
    NsdManager.RegistrationListener wfRegistrationListener;
    NsdManager.DiscoveryListener wfDiscoveryListener;
    NsdManager.ResolveListener wfResolveListener;
    NsdServiceInfo wfService;

    public static final String SERVICE_TYPE = "_ftp._tcp.";
    public static final String TAG = "NsdHelper";
    public String wfServiceName = "NsdWiFile";

    public NsdHelper() {
        initializeNsd();
    }

    public void initializeNsd() {
        initializeRegistrationListener();
        initializeDiscoveryListener();
        initializeResolveListener();
    }

    /**
     * Registers application's service on local network
     * @param port
     */
    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        serviceInfo.setServiceName(wfServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        //wfNsdManager = Context.getSystemService(Context.NSD_SERVICE);

        wfNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, wfRegistrationListener);
    }// end registerService

    public void discoverServices() {
        wfNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, wfDiscoveryListener);
    }// end discoverServices

    /**
     * Listener for service registration. Alerts Android of the success
     * or failure of the service registration and unregistration.
     */
    public void initializeRegistrationListener() {
        wfRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                wfServiceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why
                Log.e(TAG, "Registration failed: Error code: " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed! Put debugging code here to determine why.
                Log.e(TAG, "Registration failed: Error code: " + errorCode);
            }
        };
    }// end initializeRegistrationListener

    /**
     * Looks for like services on local network and archives
     */
    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoverListener;
        wfDiscoveryListener = new NsdManager.DiscoveryListener() {
            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discover started");
            }// end onDiscoverStarted

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!
                Log.d(TAG, "Service discovery success" + service);
                if(!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol
                    // and transport later for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(wfServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to.
                    Log.d(TAG, "Same machine: " + wfServiceName);
                } else if (service.getServiceName().contains("NsdWiFile")) {
                    wfNsdManager.resolveService(service, wfResolveListener);
                }
            }// end onServiceFound

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
                if (wfService == service) { wfService = null; }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discover stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                wfNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                wfNsdManager.stopServiceDiscovery(this);
            }

        };
    }// end initializeDiscoveryListener

    /**
     * Collects connection information from services on local network
     */
    public void initializeResolveListener() {
        wfResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded." + serviceInfo);

                if (serviceInfo.getServiceName().equals(wfServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                wfService = serviceInfo;
                //int port = wfService.getPort();
                //InetAddress host = wfService.getHost();
            }

        };
    }// end initializeResolveListener

    public void stopDiscovery() { wfNsdManager.stopServiceDiscovery(wfDiscoveryListener); }

    public NsdServiceInfo getChosenServiceInfo() { return wfService; }

    public void tearDown() { wfNsdManager.unregisterService(wfRegistrationListener); }

}// end class NsdHelper
