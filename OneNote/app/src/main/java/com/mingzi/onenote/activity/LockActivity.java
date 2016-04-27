package com.mingzi.onenote.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mingzi.onenote.R;

public class LockActivity extends Activity implements View.OnClickListener {

    private EditText passEdit;
    private Button loginBtn;
    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mSharedPreferences = getSharedPreferences("OneNote",MODE_PRIVATE);
        initView();
    }

    private void initView() {
        passEdit = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String inputPass = passEdit.getText().toString();
        String originPass = mSharedPreferences.getString("userPassword","");
        int len = inputPass.length();
        if (len < 4 || len > 7) {
            Toast.makeText(this,"密码长度为4~6位",Toast.LENGTH_LONG).show();
        } else {
            if (inputPass.equals(originPass)) {
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                Toast.makeText(this,"密码错误",Toast.LENGTH_LONG).show();
            }
        }

    }
}
