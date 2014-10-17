package com.example.wifile;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

/**
 * Created by Kait on 10/4/2014.
 */
public class NsdActivity extends Activity {

    NsdManager wfNsdManager;
    NsdServiceInfo wfService;
    NsdHelper wfHelper;
    int mPort;
    private String wfIP;

    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("activity created");
        super.onCreate(savedInstanceState);
        wfHelper = new NsdHelper(this);
        Server wfServer = new Server();
        findDeviceIP(this);
        mPort = wfServer.getPort();

    }

    public void findDeviceIP(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        wfIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
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
        //mConnection.tearDown();
        super.onDestroy();
    }

// end class NsdActivity
    public String getwfIP() {
        return wfIP;
    }
}

