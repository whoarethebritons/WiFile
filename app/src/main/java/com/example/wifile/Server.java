package com.example.wifile;

import android.util.Log;

import java.io.*;
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

    public void sendFiles(File inFile) {
        //ServerSocket servsock = s;
        Socket sock = null;
        OutputStream os = null;
        BufferedInputStream bis = null;
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            //file to transfer
            //this is an example file that exists on my phone
            File myFile = new File("/mnt/sdcard/download/pearing.png");

            //while statement will be changed to go through
            //a file returned from what Karen is working on


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
                Log.e(TAG, "could not complete file transfer");
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }

    }
}
