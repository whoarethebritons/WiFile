package com.example.wifile;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;


public class LauncherActivity extends PreferenceActivity{
    private static final int REQUEST_PATH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent oManager = new Intent(this, MainActivity.class);
        startActivityForResult(oManager,REQUEST_PATH);
        /*
        //gets the settings where boolean is stored about first time opening
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        //the boolean to determine if it's the first time
        //if the value doesn't exist, it assumes that its true
        boolean first_time = settings.getBoolean("firstTime", true);

        System.out.println("launcher running");
        if(!first_time) {
            Intent oManager = new Intent(this, MainActivity.class);
            startActivityForResult(oManager,REQUEST_PATH);
        }else {
            String serviceName = settings.getString("service_prefix", "Default");
            setContentView(R.layout.activity_launcher);
            EditText e = (EditText) findViewById(R.id.editText);
            /*
            Intent oManager = new Intent(this, SettingsActivity.class);
            startActivityForResult(oManager,REQUEST_PATH);
*/

            //settings.findPreferenceInHierarchy("service_prefix");
            //addPreferencesFromResource(R.xml.pref_general);
            /*
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.activity_launcher);

            settings.edit().putBoolean("firstTime", false).commit();
            //Intent oManager = new Intent(this, MainActivity.class);
            startActivityForResult(oManager,REQUEST_PATH);
        *///}
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
