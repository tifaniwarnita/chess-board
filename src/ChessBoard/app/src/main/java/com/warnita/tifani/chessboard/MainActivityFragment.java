package com.warnita.tifani.chessboard;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ImageButton startButton;
    public TextView warnMessage;
    private MainActivityFragmentListener mListener;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        warnMessage = (TextView) v.findViewById(R.id.warn_text);
        startButton = (ImageButton) v.findViewById(R.id.start_button);
        // Set up startButton onclick listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onStartButtonClicked();
                warnMessage.setVisibility(View.GONE);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Throw exception if MainActivity has not implemented the listener interface yet
        try {
            mListener = (MainActivityFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainActivityFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Interface for MainActivityFragment
    public interface MainActivityFragmentListener {
        public void onStartButtonClicked();
    }
}
