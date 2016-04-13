package com.mingzi.onenote.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

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
    private SharedPreferences mSharedPreferences;


    @Override
    public void onReceive(final Context  context, Intent intent)
    {
        mBundle = intent.getBundleExtra(RECEIVE_BUNDLE);
        mNote = mBundle.getParcelable(RECEIVE_BUNDLE);
        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(context);
        mAlertBuilder.setTitle("闹钟提示!");
        if (mNote.getNoteContent().length()>20) {
            mAlertBuilder.setMessage(mNote.getNoteContent().substring(0,20)+". . . . . .");
        } else {
            mAlertBuilder.setMessage(mNote.getNoteContent());
        }
        mAlertBuilder.setCancelable(false);
        mAlertBuilder.setPositiveButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context.getApplicationContext(), EditActivity.class);
                mBundle.putParcelable("note", mNote);
                Log.d(TAG, "onReceive: " + mNote.getNoteContent());
                i.putExtra("noteBundle", mBundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                mVibrator.cancel();
                dialog.dismiss();
            }
        });
        mAlertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mVibrator.cancel();
                dialog.dismiss();
            }
        });
        AlertDialog mAlertDialog = mAlertBuilder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.show();
        Log.d(TAG, "onReceive: "+"runs here");
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        startVibrate();
        Log.d(TAG, "onReceive: " + "runs here");
    }

    private void startVibrate()
    {
        long[] vib =
                {0, 200, 3000, 500, 2000, 1000 };
        mVibrator.vibrate(vib, 4);
    }
}
