package com.example.wifile;
import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.*;
import android.text.format.Formatter;

/**
 * Created by Kait on 9/28/2014.
 */
public class NsdActivity extends Activity {

   NsdHelper wfNsdHelper;


    //gets the local ip
    //doesn't need to be done in server
    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    String ssip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


}// end class NsdActivity
