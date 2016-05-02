package com.mingzi.onenote.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private TextView mTitleTextV;
    private TextView mContentTextV;

    private Button mCancelBtn;
    private Button mCheckBtn;
    private Vibrator mVibrator;
    private Note mNote;
    PowerManager mPowerManager;
    PowerManager.WakeLock mWakelock;
    private SharedPreferences mSharedPreferences;
    private String mStyleStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_alert);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SimpleTimer");

        Bundle mBundle = getIntent().getBundleExtra("noteBundle");
        mNote = mBundle.getParcelable("note");
        mSharedPreferences = getSharedPreferences("oneNote", Context.MODE_PRIVATE);
        if (getStyleToRemain() != null) {
            if (mStyleStr.equals("震动")) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                startVibrate();
            } else if (mStyleStr.equals("铃声")) {
                startRingToneService();
            } else if (mStyleStr.equals("铃声和震动")) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                startVibrate();
                startRingToneService();
            }
        } else {
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            startVibrate();
        }
        initView();
    }

    private void initView() {
        mTitleTextV = (TextView) findViewById(R.id.msg_title_textV);
        mTitleTextV.setText(mNote.getNoteTitle());
        mContentTextV = (TextView) findViewById(R.id.msg_content_textV);
        if (mNote.getNoteContent().length() > 20) {
            mContentTextV.setText(mNote.getNoteContent().substring(0, 20) + "......");
        } else {
            mContentTextV.setText(mNote.getNoteContent());
        }
        mCancelBtn = (Button) findViewById(R.id.alert_cancle_btn);
        mCancelBtn.setOnClickListener(this);
        mCheckBtn = (Button) findViewById(R.id.alert_ok_btn);
        mCheckBtn.setOnClickListener(this);
    }

    private void startRingToneService() {
        if (mSharedPreferences.getString("ringTone", null) != null) {
            Intent ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
            ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
            startService(ringToneServiceIntent);
        }

    }

    private String getStyleToRemain() {
        mStyleStr = mSharedPreferences.getString("styleToRemain", null);
        return mStyleStr;
    }

    @Override
    protected void onResume() {
        mWakelock.acquire();
        super.onResume();
    }


    private void startVibrate() {
        long[] vib =
                {0, 200, 3000, 500, 2000, 1000};
        mVibrator.vibrate(vib, 4);
    }

    @Override
    public void onClick(View v) {
        mWakelock.release();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent ringToneServiceIntent;
        if (v == mCancelBtn) {
            if (mStyleStr != null) {
                if (mStyleStr.equals("震动")) {
                    mVibrator.cancel();
                } else if (mStyleStr.equals("铃声")) {
                    ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
                    ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
                    stopService(ringToneServiceIntent);
                } else if (mStyleStr.equals("铃声和震动")) {
                    mVibrator.cancel();
                    ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
                    ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
                    stopService(ringToneServiceIntent);
                }
            } else {
                mVibrator.cancel();
            }


            finish();
        } else if (v == mCheckBtn) {
            if (mStyleStr != null) {
                if (mStyleStr.equals("震动")) {
                    mVibrator.cancel();
                } else if (mStyleStr.equals("铃声")) {
                    ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
                    ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
                    stopService(ringToneServiceIntent);
                } else if (mStyleStr.equals("铃声和震动")) {
                    mVibrator.cancel();
                    ringToneServiceIntent = new Intent(this, PlayRingtoneService.class);
                    ringToneServiceIntent.setAction("com.mingzi.onenote.service.PlayRingtoneService");
                    stopService(ringToneServiceIntent);
                }
            } else {
                mVibrator.cancel();
            }
            Intent viewIntent = new Intent(this, ViewNoteActivity.class);
            viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle viewBundle = new Bundle();
            viewBundle.putParcelable("note", mNote);
            viewIntent.putExtra("noteBundle", viewBundle);
            startActivity(viewIntent);
            finish();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}