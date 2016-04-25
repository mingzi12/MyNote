package com.mingzi.onenote.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.activity.MainActivity;


public class Lock extends Activity {
    private SharedPreferences fr;//查看密码用的
    private Button num1, num2, num3, n4, n5, n6, n7, n8, n9, n0, ok, b;
    private EditText mima;//密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        num1 = (Button) findViewById(R.id.num);
        num2 = (Button) findViewById(R.id.num2);
        num3 = (Button) findViewById(R.id.num3);
        n4 = (Button) findViewById(R.id.num4);
        n5 = (Button) findViewById(R.id.num5);
        n6 = (Button) findViewById(R.id.num6);
        n7 = (Button) findViewById(R.id.num7);
        n8 = (Button) findViewById(R.id.num8);
        n9 = (Button) findViewById(R.id.num9);
        n0 = (Button) findViewById(R.id.num0);
        ok = (Button) findViewById(R.id.ok);
        mima = (EditText) findViewById(R.id.mima);
        b = (Button) findViewById(R.id.b);
        a aa = new a();
        num1.setOnClickListener(aa);
        num2.setOnClickListener(aa);
        num3.setOnClickListener(aa);
        n4.setOnClickListener(aa);
        n5.setOnClickListener(aa);
        n6.setOnClickListener(aa);
        n7.setOnClickListener(aa);
        n8.setOnClickListener(aa);
        n9.setOnClickListener(aa);
        n0.setOnClickListener(aa);
        ok.setOnClickListener(aa);
        b.setOnClickListener(aa);
        //密码保存在islock.xml文件里好了
    }

    class a implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.num:
                    //Log.e("ok","ok");
                    mima.append("1");//追加1
                    break;
                case R.id.num2:
                    mima.append("2");
                    break;
                case R.id.num3:
                    mima.append("3");
                    break;
                case R.id.num4:
                    mima.append("4");
                    break;
                case R.id.num5:
                    mima.append("5");
                    break;
                case R.id.num6:
                    mima.append("6");
                    break;
                case R.id.num7:
                    mima.append("7");
                    break;
                case R.id.num8:
                    mima.append("8");
                    break;
                case R.id.num9:
                    mima.append("9");
                    break;
                case R.id.num0:
                    mima.append("0");
                    break;
                case R.id.b:
                    //回退的 功能
                    int length = mima.length();//长度
                    if (length < 1) {

                    } else {
                        String mimaxxx = mima.getText().toString();//获得字符串
                        mimaxxx = mimaxxx.substring(0, length - 1);
                        mima.setText(mimaxxx);
                    }
                    break;
                case R.id.ok:
                    String inputPass = mima.getText().toString();
                    fr = getSharedPreferences("OneNote", MODE_PRIVATE);
                    String pass = fr.getString("userPassword", "");
                    int len = inputPass.length();
                    if (len != 4) {
                        Animation animationx = AnimationUtils.loadAnimation(Lock.this, R.anim.alay);
                        mima.startAnimation(animationx);
                        Toast.makeText(Lock.this, "密码为4位数字", Toast.LENGTH_SHORT).show();

                    } else {
                        if (inputPass.equals(pass)) {
                            Intent intent = new Intent(Lock.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Animation animation = AnimationUtils.loadAnimation(Lock.this, R.anim.alay);
                            mima.startAnimation(animation);
                            Toast.makeText(Lock.this, "密码为4位数字", Toast.LENGTH_SHORT).show();
                            mima.setText("");//将编辑框处理为空
                        }
                    }
                    break;
            }
        }
    }
}
