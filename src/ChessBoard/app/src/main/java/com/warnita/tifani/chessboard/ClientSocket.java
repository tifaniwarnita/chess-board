package com.warnita.tifani.chessboard;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Tifani on 12/19/2015.
 */
public class ClientSocket extends Observable implements Runnable {
    private static Socket socket = null;
    private static Observer observer = null;

    // Socket attribute
    public static final String SERVER_ADDRESS = "xinuc.org";
    public static final int SERVER_PORT = 7387;

    // Log TAG
    private static final String TAG = ClientSocket.class.getSimpleName();

    // Open TCP connection to dedicated SERVER_ADDRESS and SERVER_PORT
    public static void openConnection() {
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_ADDRESS);
            socket = new Socket(serverAddr, SERVER_PORT);
            Log.i(TAG, "Client socket has been connected to " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (UnknownHostException unknownHostException) {
            Log.e(TAG, "openConnection()", unknownHostException);
            unknownHostException.printStackTrace();
        } catch (IOException iOException) {
            Log.e(TAG, "openConnection()", iOException);
            iOException.printStackTrace();
        }
    }

    // Receive packet from SERVER_ADDRESS:SERVER_PORT
    public static String receivePacket() {
        String packet = "";
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            packet = inFromServer.readLine();
            Log.i(TAG, "Packet received: " + packet);
        } catch (IOException e) {
            if (socket==null)
                Log.e(TAG, "receivePacket() - socket still null");
            e.printStackTrace();
        }
        return packet;
    }

    // Close any connection
    public static void closeConnection() {
        try {
            socket.close();
            socket = null;
            observer = null;
        } catch (Exception e) {
            if (socket==null)
                Log.e(TAG, "closeConnection() - socket still null");
            e.printStackTrace();
        }
    }

    // Thread for notify observer (BoardFragment)
    @Override
    public void run() {
        openConnection();
        String message;
        while (true) {
            if (socket!=null) {
                if (socket.isConnected()) {
                    message = receivePacket();
                    Log.i(TAG, message);
                    if (message != null) {
                        notifyObservers(message);
                    }
                }
            } else {
                openConnection();
            }
        }
    }

    // Override method add observer
    @Override
    public void addObserver(Observer o) {
        observer = o;
    }

    // Override method notifyObserver for notifying any update to observer
    @Override
    public void notifyObservers(Object arg) {
        if (observer!=null)
            observer.update(this, arg);
    }
}
