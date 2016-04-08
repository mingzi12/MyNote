package com.mingzi.onenote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.mingzi.onenote.R;

public class VideoViewerActivity extends AppCompatActivity {

    private VideoView vv;

    public static final String EXTRA_PATH = "path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);
        vv = (VideoView) findViewById(R.id.mVideoView);
        vv.setMediaController(new MediaController(this));

        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {
            vv.setVideoPath(path);
            vv.start();
        } else {
            finish();
        }
    }

}
