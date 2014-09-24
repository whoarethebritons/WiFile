package com.example.wifile;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.ListView;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eden on 9/22/2014.
 */
public class FileManagerActivity extends ListActivity {

    //path string
    private String mPath;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.id.list);
        String extState = Environment.getExternalStorageState();
        //set mPath to be root
        //Environment.getExternalStorageDirectory();
        mPath = "/";

        if (getIntent().hasExtra("mPath")) {
            mPath = getIntent().getStringExtra("mPath");
        }

        //sets activity title to the path name
        setTitle(mPath);

        //arrayList to store the files
        List dirs = new ArrayList();

        //to store the files
        File mFile = new File(mPath);

        //tests if you can read the file
        //or directory because file access is restricted
        //with some directories
        if (!mFile.canRead()) {
            setTitle(mPath + "(inaccessible)");
        }

        String[] list = mFile.list();

        //tests if the array is empty
        if (list != null) {
            //gets the name of every file/directory in that path
            for (String file : list) {
                //tests if it's a file we actually want the user to be able to select
                if (!file.startsWith(".")) {
                    //adds the file name to the arrayList
                    dirs.add(file);
                }
            }
        }
        //sorts the array list
        Collections.sort(dirs);

        //adapts the array list to work in list view of android
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_2, android.R.id.text1, dirs);

        //sets the list activity to use the array adapter
        setListAdapter(adapter);
    }
    public void onListItemClick(ListView l, View v, int position, long id) {
        //retrieves the file name at the position you poked
        String filename = (String) getListAdapter().getItem(position);

        //checks if path ends with '/'
        if(mPath.endsWith(File.separator)) {
            filename = mPath + filename;
        }
        //puts in the '/'
        else {
            filename = mPath + File.separator + filename;
        }

        if(new File(filename).isDirectory()) {
            //creates new intent with the path of new directory
            Intent openDir = new Intent(this, FileManagerActivity.class);
            //assigns the path to the new activity
            openDir.putExtra("mPath", filename);
            //starts activity
            startActivity(openDir);
        }
        else {
            //code for non directories here
        }
    }
}
