package com.example.wifile;

import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Eden on 9/30/2014.
 */
public class Server {
    //tag for the log
    public final String TAG = "server";


    ServerSocket servsock;

    //constructer to initialize the serversocket
    public Server() {
        try {
            servsock = new ServerSocket(0);
        }
        catch (IOException e) {
            Log.e(TAG, "could not initialize server");
        }
    }

    //to be retrieved and transfered over to the network service discovery
    public int getPort() {
        return servsock.getLocalPort();
    }
    public void sendFiles(File myFile) {
        try {
            //file to transfer
            //this is an example file that exists on my phone
            //File myFile = new File("/mnt/sdcard/download/download.jpg");

            //while statement will be changed to go through
            //the text file which will contain
            //the address/location of the file we wish to transfer

            //while (true) {

            //accept socket connection
            Socket sock = servsock.accept();

            //this sets the size of the buffer to be the size of the file
            //this allows the WHOLE file to be transferred
            sock.setSendBufferSize((int) myFile.length());

            //array to hold individual bytes
            byte[] mybytearray = new byte[(int) myFile.length()];

            //debug to see size
            System.out.println(myFile.length());

            //input stream for socket
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);

            //output stream for socket
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);

            //flushes value
            os.flush();
            sock.close();
            servsock.close();
        }catch (IOException e) {
            Log.e(TAG, "could not complete file transfer");
        }
    }
}