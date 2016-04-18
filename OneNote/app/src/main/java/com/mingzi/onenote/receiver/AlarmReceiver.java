package com.mingzi.onenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mingzi.onenote.activity.AlertActivity;
import com.mingzi.onenote.vo.Note;

/**
 * Created by Administrator on 2016/4/13.
 */

public class AlarmReceiver extends BroadcastReceiver
{


    private static final String TAG = "AlarmReceiver";
    public static final String RECEIVE_BUNDLE ="alarmReceiver";
    private Note mNote;
    private Bundle mBundle;

    @Override
    public void onReceive(final Context  context, Intent intent) {
        mBundle = intent.getBundleExtra(RECEIVE_BUNDLE);
        mNote = mBundle.getParcelable(RECEIVE_BUNDLE);
        Intent mAlertIntent = new Intent(context.getApplicationContext(), AlertActivity.class);
        mBundle.putParcelable("note", mNote);
        Log.i(TAG, "onReceive: " + mNote.getNoteContent());
        mAlertIntent.putExtra("noteBundle", mBundle);
        mAlertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mAlertIntent);

    }
}
