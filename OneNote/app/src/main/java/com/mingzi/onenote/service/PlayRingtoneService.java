package com.mingzi.onenote.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2016/4/16.
 */
public class PlayRingtoneService extends Service {

    private static final String TAG = "PlayRingtoneService";

    private SharedPreferences mSharedPreferences;
    private MediaPlayer mMediaPlayer;
    Uri mUri;

    @Override
    public void onCreate() {
        mSharedPreferences = getSharedPreferences("oneNote",MODE_PRIVATE);
        mUri = Uri.parse(mSharedPreferences.getString("ringTone",null));
        Log.d(TAG, "onCreate: "+mUri.toString());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUri == null) {
            PlayRingTone(getApplicationContext(),RingtoneManager.TYPE_RINGTONE);
        }
        else {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(),mUri);
        }
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 获取的是铃声的Uri
     * @param ctx
     * @param type
     * @return
     */
    public static Uri getDefaultRingtoneUri(Context ctx,int type) {

        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);

    }


    /**
     * 播放铃声
     * @param context
     * @param type
     */

    public void PlayRingTone(Context context,int type){
         mMediaPlayer = MediaPlayer.create(context, getDefaultRingtoneUri(context,type));
    }
}
