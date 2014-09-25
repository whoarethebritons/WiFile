package com.example.wifile;

import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Eden on 9/24/2014.
 */
public class FileService implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private int count;
    String response;

    public FileService(Socket s) {
        socket = s;
    }

    @Override
    public void run() {
        try {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                System.exit(0);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
