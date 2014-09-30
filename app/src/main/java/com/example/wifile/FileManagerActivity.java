package com.example.wifile;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Checkable;
import android.widget.ListView;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.util.SparseBooleanArray;

/**
 * Created by Eden on 9/22/2014.
 */
public class FileManagerActivity extends ListActivity {

    SparseBooleanArray mCheckStates;
    //path string
    private String mPath;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //
        //set mPath to be sdcard
        //Environment.getExternalStorageDirectory();
        mPath = Environment.getExternalStorageDirectory().getPath();

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

        File[] flist = mFile.listFiles();

        //tests if the array is empty
        if (flist != null) {
            //gets the name of every file/directory in that path
            for (File file : flist) {
                //tests if it's a file we actually want the user to be able to select
                if (!file.getName().startsWith(".")) {
                    //adds the file name to the arrayList
                    dirs.add(file.getName());
                }
            }
        }
        //sorts the array list
        Collections.sort(dirs);

        //adapts the array list to work in list view of android
        //ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_checked, android.R.id.text1, dirs);
        setListAdapter((new ArrayAdapter(this, R.layout.activity_filelist, R.id.nameView, dirs)));
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //sets the list activity to use the array adapter
        //setListAdapter(adapter);
    }

    public void getItemsChecked()
    {

    }

    //Is this method working for the checkbox? Or is to show the files within this folder?
    public void onListItemClick(ListView l, View v, int position, long id) {

<<<<<<< HEAD
        //List itemChecked = new ArrayList();
=======
      //goes into folder
>>>>>>> 271e8224fdb4a277933e042c8f11b571de49f2b3
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

        //checks if it's a folder
        if(new File(filename).isDirectory()) {
            Intent parent = getIntent();


            //creates new intent with the path of new directory
            Intent openDir = new Intent(this, FileManagerActivity.class);
            //assigns the path to the new activity
            openDir.putExtra("mPath", filename);
            //starts activity
            startActivity(openDir);
        }
        else {

        }
       // itemChecked.add(filename);
    }

<<<<<<< HEAD
=======
    public Intent getSupportParentActivityIntent () {
        return getIntent();
    }

    @Override
>>>>>>> 271e8224fdb4a277933e042c8f11b571de49f2b3
    public void setChecked(boolean checked) {

    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void toggle() {

    }

<<<<<<< HEAD
    //how to retrieve check marks?
=======



>>>>>>> 271e8224fdb4a277933e042c8f11b571de49f2b3
    public File[] getSelectedFiles(List checkDirs) {
        File[] checked = null;
        for(int i = 0; i< checkDirs.size(); i++) {
            checked[i] = (File) checkDirs.get(i);
        }
        return checked;
    }

//If a checkbox is checked then retrieve the data and store in a list. This should run if user clicks DONE
    /*public void onItemChecked(View v)
    {
        Checkbox checkbox = (Checkbox)v;
        if(checkBox.isChecked())
        {
            List selected_files = new ArrayList();
            //if it's a directory, open it and get all the file names

            //if it's only a file, get file path name
            //push directory/ file name to wherever needed
        }
    }*/

}
