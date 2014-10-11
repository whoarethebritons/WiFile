package com.example.wifile;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.*;
import android.text.format.Formatter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by Kait on 10/4/2014.
 */
public class NsdActivity extends Activity {

    NsdManager wfNsdManager;
    NsdServiceInfo wfService;
    NsdHelper wfHelper;
    private String wfIP;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wfHelper = new NsdHelper();
        findDeviceIP(this);
    }

    public void findDeviceIP(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        wfIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    //gets the local ip
    //doesn't need to be done in server
    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    String ssip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


// end class NsdActivity
    public String getwfIP() {
        return wfIP;
    }
}

