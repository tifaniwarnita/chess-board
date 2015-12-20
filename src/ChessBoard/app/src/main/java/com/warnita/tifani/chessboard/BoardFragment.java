package com.warnita.tifani.chessboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BoardFragment extends Fragment implements Observer {
    private GridLayout chessBoard;
    private ArrayList<ArrayList<ImageView>> blockMatrix = new ArrayList<>();
    private HashMap<String, Integer> resources = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> currentPosition = new HashMap<>();
    private BoardFragmentListener bListener;
    private ClientSocket clientSocket;
    private Thread socketThread;
    private String messageUpdate = "start";

    private static final int GRID_ROW = 8;
    private static final int GRID_COLUMN = 8;

    public BoardFragment() {
        // Initialize resource list (every image of blocks)
        initializeResource();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        chessBoard = (GridLayout) v.findViewById(R.id.chess_board);
        try {
            // Create board view
            createBoard(inflater, container);
            // Create new TCP Socket
            clientSocket = new ClientSocket();
            clientSocket.addObserver(this);
            socketThread = new Thread(clientSocket);
            socketThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Throw exception if MainActivity has not implemented the listener interface yet
        try {
            bListener = (BoardFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BoardFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clientSocket.closeConnection();
        clientSocket = null;
        bListener = null;
    }

    // Resource list for every possible block (the pawns)
    private void initializeResource() {
        resources.put("K", R.drawable.white_king);
        resources.put("Q", R.drawable.white_queen);
        resources.put("B", R.drawable.white_bishop);
        resources.put("N", R.drawable.white_knight);
        resources.put("R", R.drawable.white_rook);
        resources.put("k", R.drawable.black_king);
        resources.put("q", R.drawable.black_queen);
        resources.put("b", R.drawable.black_bishop);
        resources.put("n", R.drawable.black_knight);
        resources.put("r", R.drawable.black_rook);
    }

    // Create empty board (without pawn) in grid layout
    private void createBoard(LayoutInflater inflater, ViewGroup container) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels - 20;

        for (int i=0; i<GRID_ROW; i++) {
            ArrayList<ImageView> rowBlock = new ArrayList<>();
            for (int j=0; j<GRID_COLUMN; j++) {
                View v = inflater.inflate(R.layout.square_block, chessBoard, false);
                ImageView blockImage = (ImageView) v.findViewById(R.id.block_image);
                if ((i+j)%2 == 0)
                    blockImage.setBackgroundResource(R.drawable.white_block);
                else
                    blockImage.setBackgroundResource(R.drawable.black_block);
                ImageView pawnImage = (ImageView) v.findViewById(R.id.pawn);
                rowBlock.add(pawnImage); //add image block to rowBlock

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = width/GRID_ROW;
                param.width = width/GRID_COLUMN;
                param.setGravity(Gravity.CENTER);
                param.rowSpec = GridLayout.spec(i);
                param.columnSpec = GridLayout.spec(j);
                v.setLayoutParams (param);
                chessBoard.addView(v);
            }
            blockMatrix.add(rowBlock);
        }
    }

    // Doing update whenever get notify from observable object
    @Override
    public void update(Observable observable, Object data) {
        messageUpdate = (String) data;
        if (messageUpdate.equals("Not connected yet")) {
            bListener.onNotConnectedSocket();
        } else {
            bListener.onMessageUpdateReceived();
        }
    }

    // Update board whenever message received from server
    public void updateBoard() {
        try {
            List<String> position = Arrays.asList(messageUpdate.split("\\s+"));
            Log.i("BoardFragment", "Num position: " + position.size());
            int size = position.size();
            for (int i=0; i<size; i++) {
                String block = position.get(i);
                String pawn = String.valueOf(block.charAt(0));
                int row = 8 - Integer.parseInt(String.valueOf(block.charAt(2)));
                int column = ((int) block.charAt(1)) - 97; // convert from ascii char to int
                if (currentPosition.containsKey(pawn)) {
                    int currentRow = currentPosition.get(pawn).get(0);
                    int currentColumn = currentPosition.get(pawn).get(1);

                    // Set old block to empty
                    ImageView oldBlock = blockMatrix.get(currentRow).get(currentColumn);
                    oldBlock.setBackgroundResource(0);

                    // Add pawn to new block
                    ImageView newBlock = blockMatrix.get(row).get(column);
                    newBlock.setBackgroundResource(resources.get(pawn));
                    ArrayList<Integer> pos = new ArrayList<>();
                    pos.add(row); pos.add(column);
                    currentPosition.put(pawn, pos);
                } else {
                    // Add pawn to new block
                    ImageView newBlock = blockMatrix.get(row).get(column);
                    newBlock.setBackgroundResource(resources.get(pawn));
                    ArrayList<Integer> pos = new ArrayList<>();
                    pos.add(row); pos.add(column);
                    currentPosition.put(pawn, pos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BoardFragment", "Packet error");
        }

    }

    // BoardFragment listener for MainActivity
    public interface BoardFragmentListener {
        void onMessageUpdateReceived();
        void onNotConnectedSocket();
    }

}
