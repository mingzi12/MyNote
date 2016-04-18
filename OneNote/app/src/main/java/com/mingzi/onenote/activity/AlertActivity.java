package com.mingzi.onenote.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.service.PlayRingtoneService;
import com.mingzi.onenote.vo.Note;

/**
 * Created by Administrator on 2016/4/18.
 */
public class AlertActivity extends Activity implements View.OnClickListener {

    private TextView titleTextV;
    private TextView msgTextV;

    private Button cancleBtn;
    private Button okBtn;
    private Vibrator mVibrator;
    private Note mNote;
    PowerManager mPowerManager;
    PowerManager.WakeLock mWakelock;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_alert);
        Intent ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
        ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
        startService(ringToneServiceIntent);
        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        startVibrate();
        mPowerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakelock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();
        Bundle mBundle = getIntent().getBundleExtra("noteBundle");
        mNote = mBundle.getParcelable("note");

        titleTextV = (TextView) findViewById(R.id.msg_title_textV);
        titleTextV.setText(mNote.getNoteTitle());
        msgTextV = (TextView) findViewById(R.id.msg_content_textV);

        if (mNote.getNoteContent().length() > 20) {
            msgTextV.setText(mNote.getNoteContent().substring(0,20)+"......");
        } else {
            msgTextV.setText(mNote.getNoteContent());
        }


        cancleBtn = (Button) findViewById(R.id.alert_cancle_btn);
        cancleBtn.setOnClickListener(this);
        okBtn = (Button) findViewById(R.id.alert_ok_btn);
        okBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startVibrate() {
        long[] vib =
                {0, 200, 3000, 500, 2000, 1000 };
        mVibrator.vibrate(vib, 4);
    }

    @Override
    public void onClick(View v) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        if(v == cancleBtn){
            mWakelock.release();
            mVibrator.cancel();
            Intent ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
            ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
            stopService(ringToneServiceIntent);
            finish();
        }else if(v == okBtn){
            mWakelock.release();
            mVibrator.cancel();
            Intent ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
            ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
            stopService(ringToneServiceIntent);
            Intent viewIntent = new Intent(this, EditActivity.class);
            Bundle viewBundle = new Bundle();
            viewBundle.putParcelable("note",mNote);
            viewIntent.putExtra("noteBundle",viewBundle);
            startActivity(viewIntent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }


}