package com.example.wifile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class LauncherActivity extends Activity {
    private static final int REQUEST_PATH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        boolean first_time = settings.getBoolean("firstTime", true);
        System.out.println("launcher running");
        if(!first_time) {
            Intent oManager = new Intent(this, MainActivity.class);
            startActivityForResult(oManager,REQUEST_PATH);
        }else {
            /*
            Intent oManager = new Intent(this, SettingsActivity.class);
            startActivityForResult(oManager,REQUEST_PATH);
            */
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean("firstTime", false).commit();
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
