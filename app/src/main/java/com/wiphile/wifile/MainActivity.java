package com.wiphile.wifile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
    ArrayList pictures;
    private String wfIP;
    Context inContext;
    String TAG = "main";
    String serviceName;

    int requestFileMan = 1;
    int requestSettings = 2;

    NotificationManager mNotificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.myService);
        mNotify = new Notification(this);
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
        //creates chooser that allows user to select images
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),requestFileMan);
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
        securityCheck(s.getInetAddress().getCanonicalHostName(), s);
        Log.i(TAG, "ns method called, sending port");
    }

    public void nsdService() {
        inContext = this;
        //eventually some code to pull up notification icon
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        serviceName = mPref
                .getString("service_prefix", getResources()
                        .getString(R.string.pref_default_display_name)) + "WiFile";

        //initializing variables for nsd
        ListView listView = (ListView) findViewById(R.id.deviceList);
        TextView textView = (TextView) findViewById(R.id.myService);
        wfHelper = new NsdHelper(inContext, listView, textView);
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

        //without a new thread for the server transfers
        //there will be a NetworkOnMainThreadException


        newThread = new Thread(new Runnable() {

            public void run() {
            try {
                while (true) {
                    final Socket s = wfServer.getServsock().accept();
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
        //don't tear down
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (wfHelper == null) {
            wfHelper.registerService(wfPort, serviceName);
            wfHelper.discoverServices();
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
            if(resultCode == 0) {
                //none selected
                return;
            }
            if(data.getData() == null) {
                ClipData cd = data.getClipData();
                //two or more selected
                if (cd != null) {
                    for (int i = 0; i < cd.getItemCount(); i++) {
                        str.add(getImagePath(cd.getItemAt(i).getUri()));
                    }
                }
            }else {
                //one selected
                Uri cd = data.getData();
                str.add(getImagePath(cd));
            }
            if(str != null) {
                Writer fileWriter = new Writer(this, str);
                fileWriter.writeFile();
            }
        }
    }

    public void securityCheck(String str, final Socket s) {
        final String string = str;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Security");
        // set dialog message
        alertDialogBuilder
                .setMessage("Would you like to connect to: " + string)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if they want to connect, they press yes and it sends port
                        nsServer.sendPort(mPort, s);
                        return;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Thread.currentThread().interrupt();
                    }
                });

        // create alert dialog
        Looper.prepare();
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
        Looper.loop();
    }
    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();
        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }
}
