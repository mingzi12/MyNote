package com.mingzi.onenote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mingzi.onenote.R;

public class SelectFileActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "SelectFileActivity";
    private static final int GET_FILE_REQUEST_CODE = 400;

    private LinearLayout mPictureLinearLayout;
    private LinearLayout mFileLinearLayout;
    private LinearLayout mVideoLinearLayout;
    private LinearLayout mAudioLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        initView();
    }

    private void initView() {
        mPictureLinearLayout = (LinearLayout) findViewById(R.id.mPictureLinear);
        mFileLinearLayout = (LinearLayout) findViewById(R.id.mFileLinear);
        mVideoLinearLayout = (LinearLayout) findViewById(R.id.mVideoLinear);
        mAudioLinearLayout = (LinearLayout) findViewById(R.id.mAudioLinear);
        mPictureLinearLayout.setOnClickListener(this);
        mFileLinearLayout.setOnClickListener(this);
        mVideoLinearLayout.setOnClickListener(this);
        mAudioLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent selectFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        switch (v.getId()) {
            case R.id.mPictureLinear:
                selectFileIntent.setType("image/*");
                startActivityForResult(selectFileIntent, GET_FILE_REQUEST_CODE);
                break;
            case R.id.mFileLinear:
                selectFileIntent.setType("text/*");
                startActivityForResult(selectFileIntent, GET_FILE_REQUEST_CODE);
                break;
            case R.id.mVideoLinear:
                selectFileIntent.setType("video/*");
                startActivityForResult(selectFileIntent, GET_FILE_REQUEST_CODE);
                break;
            case R.id.mAudioLinear:
                selectFileIntent.setType("audio/*");
                startActivityForResult(selectFileIntent, GET_FILE_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                this.finish();
            }
            else {
                setResult(RESULT_CANCELED);
                this.finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
