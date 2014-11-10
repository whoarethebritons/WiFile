package com.example.wifile;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import java.io.File;

/**
 * Created by Kait on 10/4/2014.
 */
public class NsdActivity extends ListActivity {

    NsdManager wfNsdManager;
    NsdServiceInfo wfService;
    NsdHelper wfHelper;
    //difference is wf is for transmitting service
    //m is for server
    int mPort, wfPort;
    ArrayAdapter availableServices;
    private String wfIP;
    Server wfServer, nsServer;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //creating instances of other classes
        //necessary for file transfer
        /*
        try {
            ServerSocket s = new ServerSocket(0);
            System.out.println(wfPort);
            wfPort = s.getLocalPort();
        }catch (IOException e) {
            e.printStackTrace();
        }
        */

        wfHelper = new NsdHelper(this);
        wfServer = new Server(this);
        nsServer = new Server(this);
        wfPort = nsServer.getPort();

        //retrieve list of services
        availableServices = wfHelper.getAvailableServices();
        //retrieving port
        mPort = wfServer.getPort();
        //telling NSD what port the server is on
        wfHelper.setServerPort(mPort);

        //finds IP of current device
        //may be unnecessary with the way nsd works
        //findDeviceIP(this);

        //without a new thread for the server transfers
        //there will be a NetworkOnMainThreadException
        Thread newThread = new Thread(new Runnable() {
            public void run() {
                serverMethod();
            }
        });
        newThread.start();
        Thread nsThread = new Thread(new Runnable() {
            public void run() {
                nsMethod();
            }
        });
        nsThread.start();
    }

    //method to send files
    //needs to be converted to whatever format Karen is making
    public void serverMethod() {
        //the input file here is not used at all
        //since we're going to have a different way of doing this
        wfServer.sendFiles(new File("/mnt/sdcard"));
    }
    public void nsMethod() {
        System.out.println("sending port");
        nsServer.sendPort(mPort);
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
            //wfHelper.tearDown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wfHelper != null) {
            wfHelper.registerService(wfPort);
            wfHelper.discoverServices();
            System.out.println("wfPort: " + wfPort);
        }
    }

    @Override
    protected void onDestroy() {
        wfHelper.tearDown();
        nsServer.close();
        wfServer.close();
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

