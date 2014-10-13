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


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

<<<<<<< HEAD
       /*---------------------Get the SD card directory into a File List--------------------------*/
=======
>>>>>>> 3ef2c286dd21b86905bfce01bcb179be2a69de38
        //set mPath to be sdcard
        //Environment.getExternalStorageDirectory();
        //gets all files in the SD card
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
        //List all the files in the current directory
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
        //sorts the array list in alphabetical order
        Collections.sort(dirs);
        /*------------------------------------------------------------------------------------------*/

        //Create checkListAdaptor
        checkBox = new CheckAdapter(this,android.R.layout.simple_list_item_checked, android.R.id.text1, dirs);

        //Show the ListView and add OnClickListeners
        final ListView listView = getListView();
        listView.setOnItemClickListener(this);
    }

    //
    public static class CheckAdapter extends ArrayAdapter<String>
            implements CompoundButton.OnCheckedChangeListener
    {
        private SparseBooleanArray checkStates;

        public CheckAdapter(Context context, int resource, int textViewResourceID,String[] list)
        {
            super(context,resource,textViewResourceID,list);
            checkStates = new SparseBooleanArray(list.length);
        }
    }

    //Store the path names in a separate text file to keep a history of what was sent/synced
    public void getItemsChecked()
    {
        //filePath gets all the files in SD card
        String filePath = Environment.getExternalStorageDirectory().getPath();
        //Retrive the checked boxes
    }

    //Shows the files within each folder
    public void onListItemClick(ListView l, View v, int position, long id) {

        //List itemChecked = new ArrayList();
      //goes into folder
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

    public Intent getSupportParentActivityIntent () {
        return getIntent();
    }

    public void setChecked(boolean checked) {

    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void toggle() {

    }

    //how to retrieve check marks?
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
