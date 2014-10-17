package com.example.wifile;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import java.io.File;

/**
 * Created by Kait on 10/4/2014.
 */
public class NsdActivity extends Activity {

    NsdManager wfNsdManager;
    NsdServiceInfo wfService;
    NsdHelper wfHelper;
    int mPort;
    private String wfIP;
    Server wfServer;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //creating instances of other classes
        //necessary for file transfer
        wfHelper = new NsdHelper(this);
        wfServer = new Server();

        //retrieving port
        mPort = wfServer.getPort();
        //telling NSD what port the server is on
        wfHelper.setServerPort(mPort);

        //finds IP of current device
        //may be unnecessary with the way nsd works
        findDeviceIP(this);

        //without a new thread for the server transfers
        //there will be a NetworkOnMainThreadException
        Thread newThread = new Thread(new Runnable() {
            public void run() {
                serverMethod();
            }
        });
        newThread.start();
    }

    //method to send files
    //needs to be converted to whatever format Karen is making
    public void serverMethod() {
        //the input file here is not used at all
        //since we're going to have a different way of doing this
        wfServer.sendFiles(new File("/mnt/sdcard"));
    }

    public void findDeviceIP(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        wfIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    /*
    Added methods for when activity is paused
    resumed, or destroyed
    these register the service, discover services,
    or tear it down
     */


    @Override
    protected void onPause() {
        if (wfHelper != null) {
            wfHelper.tearDown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wfHelper != null) {
            wfHelper.registerService(mPort);
            wfHelper.discoverServices();
        }
    }

    @Override
    protected void onDestroy() {
        wfHelper.tearDown();
        super.onDestroy();
    }
    /*
    end of addition for activity methods
     */

// end class NsdActivity
    public String getwfIP() {
        return wfIP;
    }
}

