package com.example.wifile;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/*

COMPLETELY USELESS NOW
for demo purposes when server button is pressed

 */
public class ServerActivity extends ActionBarActivity {
    ServerSocket serv = null;
    Thread newThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //What does this do?
        setContentView(R.layout.activity_server);

        //Must make a new thread to do a background process on the app
        //newThread();

        newThread = new Thread(new Runnable() {
            public void run() {
                serverMethod(serv);
            }
        });
        newThread.start();

        /*
        if (android.os.Build.VERSION.SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            setContentView(R.layout.activity_server);
            serverMethod();
        }
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
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



    public void closeServ(View view) throws IOException {

        newThread.interrupt();
        String s;
        s = newThread.isAlive() ? "true" : "false";
        System.out.println(newThread.getId() + " " + newThread.getState() + " " +  s);
        serv.close();
        TextView t = (TextView) findViewById(R.id.servstat);
        t.setText("server closed");
    }

    public void reopenServ(View view) throws IOException {
        newThread.run();
        String s = newThread.isAlive() ? "true" : "false";
        System.out.println(newThread.getId() + " " + newThread.getState() + " " +  s);
    }

    public void serverMethod(ServerSocket servsock) {

        //ServerSocket servsock = s;
        Socket sock = null;
        OutputStream os = null;
        BufferedInputStream bis = null;
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            //created a server socket at port 1527
            //eventually switch to new ServerSocket(0)
            //which will have android assign it to an open port automatically
            servsock = new ServerSocket(1527);
            serv = servsock;
            TextView t1 = (TextView) findViewById(R.id.servstat);
            t1.setText( "server open");
            //String ssip = servsock.getInetAddress().getLocalHost().getHostAddress();
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ssip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            int sslp = servsock.getLocalPort();

            //the ip address of the server


            System.out.println(ssip + " " + sslp);

            //change the text view to display the ip address
            TextView t = (TextView) findViewById(R.id.Id);
            t.setText(ssip);

            //file to transfer
            //this is an example file that exists on my phone
            //File myFile = new File("/mnt/sdcard/download/download.jpg");
            /* getting a file array */

            File myFile = new File("/mnt/sdcard/download/pearing.png");

                //while statement will be changed to go through
                //an array of files
                //I think
                //while (true) {
                //accept socket connection

                sock = servsock.accept();
                //servsock.bind(sock.getLocalSocketAddress());

                //this sets the size of the buffer to be the size of the file
                //this allows the WHOLE file to be transferred
                sock.setSendBufferSize((int) myFile.length());

                //array to hold individual bytes
                byte[] mybytearray = new byte[(int) myFile.length()];

                //debug to see size
                System.out.println(myFile.length());

                //input stream for socket
                bis = new BufferedInputStream(new FileInputStream(myFile));
                bis.read(mybytearray, 0, mybytearray.length);

                //output stream for socket
                os = sock.getOutputStream();

                os.write(mybytearray, 0, mybytearray.length);
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
                //flushes value
            try {
                os.flush();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
            }

    }

}
