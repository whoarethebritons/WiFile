package com.example.wifile;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends Activity {
    //private static final int REQUEST_PATH = 1;
    //nsd variables
    NsdManager wfNsdManager;
    NsdServiceInfo wfService;
    NsdHelper wfHelper;

    //difference is wf is for transmitting service
    //m is for server
    int mPort, wfPort;
    Server wfServer, nsServer;
    Thread newThread, nsThread;
    static Notification mNotify;
    ArrayList availableServices;
    private String wfIP;
    Context inContext;
    String TAG = "main";
    String serviceName;

    int requestFileMan = 1;
    int requestSettings = 2;

    NotificationManager mNotificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("I should be doing something");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotify = new Notification(this);
        mNotify.notify(2, "test");
        //startActivity(NotificationActivity, )

        nsdService();
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
            //open the file manager "explorer"
            Intent oManager = new Intent(this, SettingsActivity.class);
            //wait for the selected folders
            startActivityForResult(oManager, requestSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //filechooser activity/intent
    public void getFile(View view){
        //open the file manager "explorer"
        Intent openManager = new Intent(this, FileManagerActivity.class);
        //wait for the selected folders
        startActivityForResult(openManager,requestFileMan);
    }


    /*background methods for service to run*/

    //method to send files
    //needs to be converted to whatever format Karen is making
    public void serverMethod(Socket s) {
        Log.i(TAG, "server method called");
        //the input file here is not used at all
        //since we're going to have a different way of doing this
        wfServer.sendFiles(s);
    }

    //method to send port number
    public void nsMethod(Socket s) {
        Log.i(TAG, "ns method called, sending port");
        nsServer.sendPort(mPort, s);
    }

    public void nsdService() {
        inContext = this;
        //eventually some code to pull up notification icon
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        serviceName = mPref
                .getString("service_prefix", getResources()
                        .getString(R.string.pref_default_display_name)) + "WiFile";
        /*
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                //testing to see if the preference for broadcast name was changed
                if(key.equals("service_prefix")) {
                    wfHelper.setWfServiceName(prefs
                            .getString(key, getResources()
                                    .getString(R.string.pref_default_display_name)) + "WiFile");
                }
            }
        };
        mPref.registerOnSharedPreferenceChangeListener(listener);*/
        System.out.println(serviceName);

        //initializing variables for nsd
        ListView listView = (ListView) findViewById(R.id.deviceList);
        wfHelper = new NsdHelper(inContext, listView);
        Log.i(TAG, "helper created");

        wfServer = new Server(inContext);
        Log.i(TAG, "wfserver created");

        //retrieving port
        mPort = wfServer.getPort();
        Log.i(TAG, "wfserver gotten");

        //telling NSD what port the server is on
        wfHelper.setServerPort(mPort);
        Log.i(TAG, "setserverport called");


        nsServer = new Server(this);
        Log.i(TAG, "nserver created");
        wfPort = nsServer.getPort();
        Log.i(TAG, "wfport retrieved");


        //start discovery
        wfHelper.registerService(wfPort, serviceName);
        wfHelper.discoverServices();
        System.out.println("wfPort: " + wfPort);

        //without a new thread for the server transfers
        //there will be a NetworkOnMainThreadException


        newThread = new Thread(new Runnable() {

            public void run() {
            try {
                while (true) {
                    final Socket s = wfServer.getServsock().accept();
                    //dialogCreator("this one");
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            serverMethod(s);
                        }
                    });
                    t.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        });


        nsThread = new Thread(new Runnable() {
            public void run() {
            try {
                while (true) {
                    final Socket s = nsServer.getServsock().accept();
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            nsMethod(s);
                        }
                    });
                    t.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        });
        nsThread.start();
        Log.i(TAG, "nsThread running " + nsThread.isAlive());
        newThread.start();
        Log.i(TAG, "newThread running " + newThread.isAlive());
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
        if (wfHelper == null) {
            wfHelper.registerService(wfPort, serviceName);
            wfHelper.discoverServices();
            System.out.println("wfPort: " + wfPort);
        }
        super.onResume();
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

    //get result of choose files
    //which is stored in an arraylist
    //this arraylist is then sent to the file writer
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == requestFileMan) {
            ArrayList<String> str = new ArrayList<String>();
            ArrayList<String> strings = data.getStringArrayListExtra("mFileNames");
            str.addAll(strings);
            for (String s : str) {
                System.out.println(s);
            }
            Writer fileWriter = new Writer(this, str);
            fileWriter.writeFile();
        }
    }
/*
    //creates an okay/cancel dialog
    public void dialogCreator(String s) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Would you like to connect to: ");

        // set dialog message
        alertDialogBuilder
                .setMessage(s)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        //finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }*/
}
