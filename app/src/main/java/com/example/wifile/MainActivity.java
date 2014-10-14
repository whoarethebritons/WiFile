package com.example.wifile;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    private static final int REQUEST_PATH = 1;
    NotificationManager mNotificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //eventually some code to pull up notification icon
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    //filechooser activity/intent
    public void getFile(View view){
        //open the file manager "explorer"
        Intent openManager = new Intent(this, FileManagerActivity.class);
        //wait for the selected folders
        startActivityForResult(openManager,REQUEST_PATH);
    }
    public void startServer(View view){

            //open the file manager "explorer"
            Intent oManager = new Intent(this, ServerActivity.class);
            //wait for the selected folders
            startActivityForResult(oManager,REQUEST_PATH);
        }
    public void startService(View view){

        //open the file manager "explorer"
        Intent oManager = new Intent(this, NsdActivity.class);
        //wait for the selected folders
        startActivityForResult(oManager,REQUEST_PATH);
    }

}
