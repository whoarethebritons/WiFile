package com.example.wifile;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Checkable;
import android.widget.ListView;
import android.os.Environment;
import java.io.*;
import java.util.*;
import android.util.SparseBooleanArray;

/**
 * FileManagerActivity is responsible for retrieving the selected files from the checkboxes
 * and storing those selected files within a TEXT FILE. MAKE A FOLDER PUT A FILE IN THERE!!!!!!!
 * Created by Eden on 9/22/2014.
 */
public class FileManagerActivity extends ListActivity {

    private CheckAdapter checkAdapter;
    private SparseBooleanArray checkStates;
    private String mPath;
    //Create the history file, where we will store the file names, which will be added
    // to the computer. The file will be stored on the the phone's sd card
    String FILEHISTORY = "fileHistory";
    String[] fileDirectory;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /*---------------------Get the SD card directory into a File List--------------------------------------*/
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
        Object[] storeDirs = dirs.toArray(); //Convert List to an object array

        fileDirectory = Arrays.copyOf(storeDirs, storeDirs.length,String[].class);//Convert to type String

        /*-----------------------------------------------------------------------------------------------------------*/
        //ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_checked, android.R.id.text1, dirs);
       // setListAdapter((new ArrayAdapter(this, R.layout.activity_filelist, R.id.nameView, dirs)));

        //Create checkListAdaptor. Similar to an ArrayAdapter, with SparseBooleanArray added to it
        checkAdapter = new CheckAdapter(this, R.layout.activity_filelist, R.id.nameView,
                fileDirectory);
        setListAdapter(checkAdapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //(Context context, int resource, int textViewResourceID, T[] objects )
        //(context, references a single textview, field ID references a textview in the larger layout resource,
        // array of objects to store into arrayadapter)
    }

    //CheckAdapter is for each checkbox in the list
    public class CheckAdapter extends ArrayAdapter<String> {
        Context context;
        int resource;
        int textViewResourceID;

        public CheckAdapter(Context context, int resource, int textViewResourceID, String[] list) {
            super(context, resource, textViewResourceID, list);
            checkStates = new SparseBooleanArray(list.length);
        }

        public boolean isChecked(int position) {
            return checkStates.get(position, false);
        }

        public void setChecked(int position, boolean checked) {
            checkStates.put(position, checked);
        }

        public void toggle(int position, boolean checked) {
            setChecked(position, !checked);
        }
    }
    //DONE button listener will retrieve all checked files
    public void finish()
    {
        //Collect all the files
        //send all filenames to writeToFile(String [] fileList)
    }

    //Writes to the internal file, FILEHISTORY
    public void writeToFile() throws IOException
    {
        String string = "some filename";
        FileOutputStream fos = openFileOutput(FILEHISTORY, Context.MODE_PRIVATE);
        try {
            fos.write(string.getBytes());
        } catch(IOException e) {

        }
        fos.close();
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

}