package com.wiphile.wifile;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Eden on 9/30/2014.
 * Server sends either the port or files
 * over .ftp.tcp.
 */
public class Server {
    //tag for the log
    public final String TAG = "server";
    //name of file that has the files to send over the port
    String FILEHISTORY = "fileHistory";
    ServerSocket servsock;

    //context is required so that the FileInputStream with PRIVATE_MODE
    //that contains the files to send over the port
    Context mContext;

    public Server(Context c) {
        mContext = c;
        try {
            servsock = new ServerSocket(0);
        }
        catch (IOException e) {
            Log.e(TAG, "could not initialize server");
        }
    }

    //to be retrieved and transferred over to the network service discovery
    public int getPort() {
        return servsock.getLocalPort();
    }


    //needs to pass in socket as parameter so that server can accept multiple connections
    public void sendPort(int mPort, Socket inSock) {
        //securityCheck(inSock.toString());
        MainActivity.mNotify.notify(1, "Connecting");
        Socket sock = inSock;
        String s = sock.toString();
        //dialogCreator(s);
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
        //notifications
        MainActivity.mNotify.notify(1, "Transferring Files");

        //socket of client connecting
        Socket sock = inSock;

        //to read from the files to send
        FileInputStream fileForServer;
        try{
            //testing: Log.v(TAG, "trying");

            //number of files to send
            int numOfFiles = 0;

            //ArrayList of Strings of file names
            ArrayList<String> filesToSend = new ArrayList<String>();

            //initializes the FileInputStream for the files to send
            fileForServer = mContext.openFileInput(FILEHISTORY);
            InputStreamReader isr = new InputStreamReader ( fileForServer ) ;
            BufferedReader filebuff = new BufferedReader(isr);

            //reads first line of file
            String readString = filebuff.readLine ( ) ;

            //gets input and output streams for socket
            DataOutputStream sockOutput = new DataOutputStream(sock.getOutputStream());
            DataInputStream sockInput = new DataInputStream(sock.getInputStream());

            //reading the strings from the file
            while(readString != null) {
                //add to ArrayList
                filesToSend.add(readString);
                //number of files to send increases
                numOfFiles++;
                //read next line for loop
                readString = filebuff.readLine();

            }
            //closes the Readers for file
            isr.close ( ) ;
            filebuff.close();
            //testing:
            Log.v(TAG,"number of files " + numOfFiles);

            //sends the number of files that are going to be sent to client
            sockOutput.writeInt(numOfFiles);
            sockOutput.flush();

            //receives the number of files that have been received
            int filesReceived = sockInput.readInt();

            //boolean value to see if it's time to send a different file
            boolean same = false;

            while(filesReceived < numOfFiles) {
                //if the number read from the client is not the same number it just was
                if(!same) {
                    try {
                        //gets the first file in the string ArrayList
                        File myFile = new File(filesToSend.get(filesReceived));
                        //sends the file
                        sendSingleFile(myFile, sockOutput);
                        //temporary to store current value from client
                        int temp = filesReceived;
                        //gets new value
                        filesReceived = sockInput.readInt();
                        //this is basically to make sure it doesn't keep
                        //sending the same file
                        if (temp == filesReceived) {
                            same = true;
                        }
                        //testing:
                        Log.i(TAG, "client is connecting");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    //testing:
                    Log.v(TAG, "file already sent");
                    filesReceived = sockInput.readInt();
                }
            }
            sockOutput.close();
            Log.v(TAG, "server is done");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                Log.v(TAG, "socket close");
                sock.close();
                //notification
                MainActivity.mNotify.notify(1, "Transfer Complete");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendSingleFile(File file, DataOutputStream dos) throws IOException {
        //checks for non-existant variables
        if (dos != null && file.exists() && file.isFile()) {
            //to read file that's going to be sent
            FileInputStream input = new FileInputStream(file);
            //byte array to store bytes of file to be sent over
            byte[] mybytearray = new byte[(int) file.length()];
            //writes length of byte array to socket
            dos.writeLong(file.length());
            //writes file name to socket
            dos.writeUTF(file.getName());

            //initialize streams for file reader
            BufferedInputStream buffStream = new BufferedInputStream(input);
            DataInputStream dataStream = new DataInputStream(buffStream);
            //reads all the bytes into the byte[] from 0 to the length of file
            dataStream.readFully(mybytearray, 0, mybytearray.length);
            //close stream
            dataStream.close();

            //writes bytes to socket
            dos.write(mybytearray, 0, mybytearray.length);
            //flushes
            dos.flush();
            //closes file stream
            input.close();
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