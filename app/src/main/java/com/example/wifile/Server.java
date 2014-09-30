package com.example.wifile;

import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by Eden on 9/30/2014.
 */
public class Server {
    public final String TAG = "server";
    ServerSocket servsock;
    public Server() {
        try {
            servsock = new ServerSocket(0);
        }
        catch (IOException e) {
            Log.e(TAG, "could not initialize server");
        }
    }
    public int getPort() {
        return servsock.getLocalPort();
    }
}
