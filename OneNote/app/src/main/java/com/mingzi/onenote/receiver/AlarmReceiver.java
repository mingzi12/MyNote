package com.mingzi.onenote.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.mingzi.onenote.activity.EditActivity;
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
    private Vibrator mVibrator;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        Log.d(TAG, "onReceive: "+"runs here");
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        startVibrate();
        Log.d(TAG, "onReceive: "+"runs here");
        mBundle = intent.getBundleExtra(RECEIVE_BUNDLE);
        mNote = mBundle.getParcelable(RECEIVE_BUNDLE);
        Intent i = new Intent(context.getApplicationContext(), EditActivity.class);
        mBundle.putParcelable("note",mNote);
        Log.d(TAG, "onReceive: "+mNote.getNoteContent());
        i.putExtra("noteBundle",mBundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void startVibrate()
    {
        long[] vib =
                {0, 200, 3000, 500, 2000, 1000 };
        mVibrator.vibrate(vib, 4);
    }
}
