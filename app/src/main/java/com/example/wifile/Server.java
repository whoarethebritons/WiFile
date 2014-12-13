package com.example.wifile;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Eden on 9/30/2014.
 */
public class Server {
    //tag for the log
    public final String TAG = "server";
    String FILEHISTORY = "fileHistory";
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
    public void sendFiles(Socket inSock) {
        Socket sock = inSock;
        //to write to port
        OutputStream os = null;
        //to read from file
        BufferedInputStream bis = null;
        FileInputStream fis;
        try{
            Log.v(TAG, "trying");
            ArrayList<String> filesToSend = new ArrayList<String>();
            fis = mContext.openFileInput(FILEHISTORY);
            InputStreamReader isr = new InputStreamReader ( fis ) ;
            BufferedReader filebuff = new BufferedReader(isr);
            String readString = filebuff.readLine ( ) ;

            int numOfFiles = 0;

            //Sending file name and file size to the server
            DataOutputStream sockOutput = new DataOutputStream(sock.getOutputStream());
            DataInputStream sockInput = new DataInputStream(sock.getInputStream());
            int i = 1;
            while(readString != null) {
                filesToSend.add(readString);
                numOfFiles++;
                readString = filebuff.readLine();
            }
            Log.v(TAG,"number of files " + numOfFiles);
            sockOutput.writeInt(numOfFiles);
            sockOutput.flush();
            int filesReceived = sockInput.readInt();
            boolean same = false;

            while(filesReceived < numOfFiles) {
                System.out.println(i + ". " + filesToSend.get(filesReceived));
                if(!same) {
                    try {
                        File myFile = new File(filesToSend.get(filesReceived));
                        sendSingleFile(myFile, sockOutput);
                        int temp = filesReceived;
                        filesReceived = sockInput.readInt();
                        System.out.println(filesReceived);
                        if (temp == filesReceived) {
                            same = true;
                        }
                        Log.i(TAG, "something is connecting");

                    } catch (IOException e) {
                        e.printStackTrace();
                        filesReceived = sockInput.readInt();
                    }
                }
                else {
                    System.out.println("file already sent");
                }

            }
            sockOutput.close();
            System.out.println("readString was null");

            isr.close ( ) ;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File location = mContext.getFilesDir();

        if (Thread.currentThread().isInterrupted()) {
            return;
        }


    }

    public void sendSingleFile(File file, DataOutputStream dos) throws IOException {
        if (dos != null && file.exists() && file.isFile()) {
            FileInputStream input = new FileInputStream(file);
            byte[] mybytearray = new byte[(int) file.length()];
            dos.writeLong(file.length());
            dos.writeUTF(file.getName());
            System.out.println(file.getAbsolutePath());
            //int read = 0;
            BufferedInputStream dbis = new BufferedInputStream(input);
            DataInputStream dis = new DataInputStream(dbis);
            dis.readFully(mybytearray, 0, mybytearray.length);
            dis.close();
            dos.write(mybytearray, 0, mybytearray.length);

            /*while ((read = input.read()) != -1)
                dos.writeByte(read);*/
            dos.flush();
            input.close();
            System.out.println("File successfully sent!");
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