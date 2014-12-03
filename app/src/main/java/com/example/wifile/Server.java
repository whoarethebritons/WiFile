package com.example.wifile;

import android.content.Context;
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
    Context mContext;
    Server() {}
    public Server(Context c) {
        mContext = c;
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


    //needs to pass in socket as parameter so that server can accept multiple connections
    public void sendPort(int mPort, Socket inSock) {
        Socket sock = inSock;
        DataOutputStream dos = null;

        //code to be perfected about server interrupt
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            //file to transfer
            //this is an example file that exists on my phone
            Log.i(TAG, "port: " + mPort);

            //socket won't be very long
            sock.setSendBufferSize(10);

            //output stream for socket
            dos = new DataOutputStream(sock.getOutputStream());

            //writes value to port
            dos.writeInt(mPort);
            //flushes
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            //closes streams and socket
            try {
                dos.close();
                sock.close();
                Log.i(TAG, "port transfer completed");
            } catch (IOException e) {
                Log.e(TAG, "could not complete file transfer");
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    //needs socket as parameter so that it can have multiple connections
    public void sendFiles(File inFile, Socket inSock) {
        Socket sock = inSock;
        //to write to port
        OutputStream os = null;
        //to read from file
        BufferedInputStream bis = null;
        File location = mContext.getFilesDir();

        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            //file to transfer
            //this is an example file that exists on my phone
            Log.d(TAG, location.getPath() + "/filehistory.txt");
            File myFile = new File("/mnt/sdcard/download/pearing.png");

            //FileInputStream serverFileStream = mContext.openFileInput("filehistory.txt");

            //while statement will be changed to go through
            //a file returned from what Karen is working on

            Log.i(TAG,"something is connecting");

            //this sets the size of the buffer to be the size of the file
            //this allows the WHOLE file to be transferred
            sock.setSendBufferSize((int) myFile.length());

            //array to hold individual bytes
            byte[] mybytearray = new byte[(int) myFile.length()];

            //debug to see size
            System.out.println(myFile.length());
            Log.d(TAG, "length: " + myFile.length());

            //input stream for socket
            bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);

            //output stream for socket
            os = sock.getOutputStream();

            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            //flushes value
            try {
                os.close();
                bis.close();
                sock.close();
            } catch (IOException e) {
                Log.e(TAG, "could not complete file transfer");
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    public ServerSocket getServsock() {
        return servsock;
    }

    public void close() {
        try {
            servsock.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}