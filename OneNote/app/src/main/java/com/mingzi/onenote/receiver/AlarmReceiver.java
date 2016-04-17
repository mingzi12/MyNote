package com.mingzi.onenote.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

import com.mingzi.onenote.activity.EditActivity;
import com.mingzi.onenote.service.PlayRingtoneService;
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
    public void onReceive(final Context  context, Intent intent)
    {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();
        final Intent ringToneServiceIntent = new Intent(context, PlayRingtoneService.class);
        ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
        context.startService(ringToneServiceIntent);
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
                context.stopService(new Intent("com.mingzi.onenote.service.PlayRingtoneService"));
                mWakelock.release();
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
                context.stopService(new Intent("com.mingzi.onenote.service.PlayRingtoneService"));
                mWakelock.release();
                mVibrator.cancel();
                dialog.dismiss();
            }
        });
        AlertDialog mAlertDialog = mAlertBuilder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                |WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
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
