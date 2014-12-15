package com.example.wifile;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//import android.support.v7.app.ActionBarActivity;

/**
 * FileManagerActivity is responsible for retrieving the selected files from the checkboxes
 * and storing those selected files within a file.
 * Created by Eden on 9/22/2014.
 */
public class FileManagerActivity extends ListActivity {

    private CheckAdapter checkAdapter;
    private SparseBooleanArray checkStates;
    private String mPath;
    private ArrayList<String> mFileNames;
    String TAG = "fileman";

    //Create the history file, where we will store the file names, which will be added
    // to the computer. The file will be stored on the the phone's sd card
    String FILEHISTORY = "fileHistory";
    String[] fileDirectory;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_manager, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        //writes file
        writeFile();
        //readFiles();
        setResult(RESULT_OK, getIntent());
        //closes activity
        this.finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_done:
                //writes file
                writeFile();
                //readFiles();
                setResult(RESULT_OK, getIntent());
                //closes activity
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /*---------------------Get the SD card directory into a File List--------------------------------------*/
        //set mPath to be sdcard
        //Environment.getExternalStorageDirectory();
        //gets all files in the SD card
        //mFileNames = new ArrayList<String>();
        mPath = Environment.getExternalStorageDirectory().getPath();

        //gets the folder
        if (getIntent().hasExtra("mPath")) {
            mPath = getIntent().getStringExtra("mPath");
        }

        //if the ArrayList does not exist, create it
        if(mFileNames == null) {
            mFileNames = new ArrayList<String>();
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

        //takes in context in order to create a SparseBooleanArray that
        //stores the string and value of true or false for the list of files
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

        public void toggle(int position) {
            //isChecked gets current value of checkbox
            //inverts it to "toggle"
            //sets it to opposite value
            setChecked(position, !isChecked(position));
        }
    }
    //DONE button listener will retrieve all checked files
    //the back button will also do this

    public void writeFile() {
        //Collect all the files
        //send all filenames to writeToFile(String [] fileList)

        //checks size of array
        for(int i = 0; i < checkStates.size(); i++) {
            //checks if the the sparse boolean array has value of true
            //meaning it was checked and the file string
            if(checkStates.valueAt(i)) {
                //gets the string at that location, adds it to the arraylist
                //all activities have this arraylist
                String string = mPath +"/" + fileDirectory[checkStates.keyAt(i)]+ "\n";
                mFileNames.add(string);
                //this allows the child to get the ArrayList
                getIntent().putStringArrayListExtra("mFileNames", mFileNames);
            }
        }
    }

    public void onCheckBox(View view) {
        //gets which row: position & filename
        final int position = getListView().getPositionForView((View) view.getParent());
        String filename = (String) getListAdapter().getItem(position);

        //toggles the thing
        checkAdapter.toggle(position);
    }


    //Shows the files within each folder
    public void onListItemClick(ListView l, View v, int position, long id) {
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
            startActivityForResult(openDir, 1);
        }
    }

    public Intent getSupportParentActivityIntent () {
        return getIntent();
    }

    //when one of the FileManagerActivities returns, it adds to the ArrayList
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //this gets the ArrayList from the child
        ArrayList<String> strings = data.getStringArrayListExtra("mFileNames");
        
        //add all the child activity's selections to the current directory's
        mFileNames.addAll(strings);
        //and returns result
        setResult(RESULT_OK, getIntent());

        // testing purposes
        for (String s : mFileNames) {
            Log.v(TAG, data + "here:" + s);
        }

        //puts the extra ArrayList
        getIntent().putStringArrayListExtra("mFileNames", mFileNames);
        setResult(RESULT_OK, getIntent());
    }
}