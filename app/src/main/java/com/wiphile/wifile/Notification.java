package com.wiphile.wifile;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.io.File;

/*
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
*/
public class Notification {
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    SharedPreferences mPref;
    Context mContext;
    public Notification(Context inContext) {
        mContext = inContext;
        mBuilder = new NotificationCompat.Builder(inContext);
        //the pear icon
        mBuilder.setSmallIcon(R.drawable.pearing);
        //displays wiFile
        mBuilder.setContentTitle("WiFile");
        //displays what it's doing
        //mBuilder.addAction();
        mPref = PreferenceManager.getDefaultSharedPreferences(inContext);

        mNotificationManager = (NotificationManager)inContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(0, mBuilder.build());
        // mId allows you to update the notification later on.
    }
    public void notify(int id, String string) {
        if(mPref.getBoolean("notifications_wifile", true)) {
            mBuilder.setContentText(string);
            String ringtone = mPref.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound");
            //if(ringtone.equals("content://settings/system/notification_sound")) {
            System.out.println(ringtone);
            Uri ring = Uri.fromFile(new File(ringtone));
            mBuilder.setSound(ring);
            //}
            android.app.Notification note = mBuilder.build();
            int vibrate = 0;

            if (mPref.getBoolean("notifications_new_message_vibrate", true)) {
                vibrate = android.app.Notification.DEFAULT_VIBRATE;
            }

            //+ "WiFile";);//android.app.Notification.DEFAULT_SOUND);

            note.defaults |= vibrate;
            //note.defaults |= ringtone;


            mNotificationManager.notify(id, note);
        }
    }
}
