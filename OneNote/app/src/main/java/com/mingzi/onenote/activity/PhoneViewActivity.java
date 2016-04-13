package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.MyBitmap;

public class PhoneViewActivity extends Activity {

    private ImageView imageView;
    Bitmap bitmap;
    public static final String EXTRA_PATH = "path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("返回");
        setContentView(R.layout.activity_phone_viewer);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.phone_view_image);
        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {

            bitmap = MyBitmap.getBitmapByPath(path);
            imageView.setImageBitmap(bitmap);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home :
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        bitmap.recycle();
        super.onDestroy();
    }
}
