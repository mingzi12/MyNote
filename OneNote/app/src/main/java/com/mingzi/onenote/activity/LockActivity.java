package com.mingzi.onenote.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.mingzi.onenote.util.Lock;

public class LockActivity extends Activity {
    private static final String TAG = "LockActivity";
    private SharedPreferences.Editor editor;//写文件
    private SharedPreferences fr;
    private boolean is = false;//是否需要密码保护

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // editor = getSharedPreferences("OneNote",MODE_PRIVATE).edit().putBoolean("isLock",false);
      //  editor.commit();
        fr = getSharedPreferences("OneNote", MODE_PRIVATE);
        is = fr.getBoolean("isLock", false);//取出值 查看
        Log.d(TAG, "onCreate: "+is);
        if (is) {
            Intent intent = new Intent(this, Lock.class);
            startActivity(intent);
            finish();
        } else if (!is) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);//跳转
            finish();
        }

    }
}

