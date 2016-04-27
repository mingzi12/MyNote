package com.mingzi.onenote.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.BitmapUtils;
import com.mingzi.onenote.view.MyImageView;


public class PhoneViewActivity extends Activity {

    public static final String EXTRA_PATH = "path";
    MyImageView imageView;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_viewer);
        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {

            bitmap = BitmapUtils.getBitmapByPath(path);
        } else {
            finish();
        }
        findView();

    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        init();
    }



    private void findView() {
        imageView = (MyImageView) findViewById(R.id.phone_view_image);
    }

    private void init() {

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int screenW = this.getWindowManager().getDefaultDisplay().getWidth();
        int screenH = this.getWindowManager().getDefaultDisplay().getHeight()
                - statusBarHeight;
        if (bitmap != null) {
            imageView.imageInit(bitmap, screenW, screenH, statusBarHeight);

        }
        else
        {
            Toast.makeText(this, "图片加载失败，请稍候再试！", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                imageView.mouseDown(event);
                break;

            /**
             * 非第一个点按下
             */
            case MotionEvent.ACTION_POINTER_DOWN:

                imageView.mousePointDown(event);

                break;
            case MotionEvent.ACTION_MOVE:
                imageView.mouseMove(event);

                break;

            case MotionEvent.ACTION_UP:
                imageView.mouseUp();
                break;

        }

        return super.onTouchEvent(event);
    }
}  