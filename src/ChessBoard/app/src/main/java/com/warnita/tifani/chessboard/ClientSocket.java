package com.warnita.tifani.chessboard;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Tifani on 12/19/2015.
 */
public class ClientSocket {
    private static Socket socket = null;
    public static final String SERVER_ADDRESS = "xinuc.org";
    public static final int SERVER_PORT = 7387;

    private static final String TAG = ClientSocket.class.getSimpleName();

    public static void openConnection() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            Log.i(TAG, "Client socket has been connected to " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (UnknownHostException unknownHostException) {
            Log.e(TAG, "openConnection()", unknownHostException);
            unknownHostException.printStackTrace();
        } catch (IOException iOException) {
            Log.e(TAG, "openConnection()", iOException);
            iOException.printStackTrace();
        }
    }

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

    public static void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            if (socket==null)
                Log.e(TAG, "receivePacket() - socket still null");
            e.printStackTrace();
        }
    }
}
