package com.warnita.tifani.chessboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.MainActivityFragmentListener,
        BoardFragment.BoardFragmentListener {
    private BoardFragment boardFragment;
    private MainActivityFragment mainActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        mainActivityFragment = new MainActivityFragment();

        if (fragment == null) {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, mainActivityFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartButtonClicked() {
        FragmentManager fm = getSupportFragmentManager();

        boardFragment = new BoardFragment();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.fragment_container, boardFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMessageUpdateReceived() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boardFragment.updateBoard();
            }
        });
    }

    @Override
    public void onNotConnectedSocket() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivityFragment.warnMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}
