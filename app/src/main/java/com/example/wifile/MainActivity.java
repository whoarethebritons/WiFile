package com.example.wifile;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.io.File;

public class MainActivity extends Activity {
    private static final int REQUEST_PATH = 1;

    NsdManager wfNsdManager;
    NsdServiceInfo wfService;
    NsdHelper wfHelper;
    //difference is wf is for transmitting service
    //m is for server
    int mPort, wfPort;
    ArrayAdapter availableServices;
    private String wfIP;
    Server wfServer, nsServer;
    Thread newThread, nsThread;

    NotificationManager mNotificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //eventually some code to pull up notification icon

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
        newThread = new Thread(new Runnable() {
            public void run() {
                serverMethod();
            }
        });
        newThread.start();
        nsThread = new Thread(new Runnable() {
            public void run() {
                nsMethod();
            }
        });
        nsThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //filechooser activity/intent
    public void getFile(View view){
        //open the file manager "explorer"
        Intent openManager = new Intent(this, FileManagerActivity.class);
        //wait for the selected folders
        startActivityForResult(openManager,REQUEST_PATH);
    }
    public void startServer(View view){

            //open the file manager "explorer"
            Intent oManager = new Intent(this, ServerActivity.class);
            //wait for the selected folders
            startActivityForResult(oManager,REQUEST_PATH);
        }

    /* REMOVED DUE TO NSD MOVED TO MAIN
    public void startService(View view){
        //open the file manager "explorer"
        Intent oManager = new Intent(this, NsdActivity.class);
        //wait for the selected folders
        startActivityForResult(oManager,REQUEST_PATH);
    }
    /*

     */
    /*background methods for service to run*/

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

    // end class NsdActivity
    public String getwfIP() {
        return wfIP;
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
}
