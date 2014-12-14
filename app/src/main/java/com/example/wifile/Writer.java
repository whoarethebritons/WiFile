package com.example.wifile;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Eden on 12/8/2014.
 * Writes file names to a private file only
 * accessible by this application
 */
public class Writer {
    Context mContext;
    ArrayList<String> mArrayList;
    private String FILEHISTORY = "fileHistory";
    private String TAG = "writer";
    public Writer(Context c, ArrayList<String> s) {
        mContext = c;
        mArrayList = s;
    }

    public void writeFile()
    {
        //file output
        FileOutputStream fileOutput = null;
        try {
            fileOutput = mContext.openFileOutput(FILEHISTORY, Context.MODE_PRIVATE);


        //Collect all the files
        //send all filenames to writeToFile(String [] fileList)

        //checks size of array
            for(String string: mArrayList) {
                Log.v(TAG, "file to write: " + string) ;
                fileOutput.write(string.getBytes(),0,string.getBytes().length);
            }
        }catch(IOException e) {
            Log.e("fileman","IOEXCEPTION");
        }finally{
            try {
                fileOutput.close();
            }catch(IOException e) {
                Log.e("fileman","IOEXCEPTION");
            }
        }

    }
}
