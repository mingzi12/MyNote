package com.mingzi.onenote.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.BitmapUtils;
import com.mingzi.onenote.util.MediaDBAccess;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Media;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewNoteActivity extends Activity implements ImageView.OnClickListener {

    public static final String TAG = "ViewNoteActivity";
    public static final String VIEW_NOTE_BUNDLE = "view";

    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private TextView mTitleView;
    private TextView mContentView;
    private MediaDBAccess mMediaDBAccess;
    private NoteDBAccess mNoteDBAccess;
    private List<Media> mMediaList;

    private Note mNote;
    private List<Bitmap> mBitmaps;
    private List<String> mPathsList;        // 存放和当前便签匹配的所有图片或者视频的路径
    private int mCurrentNoteId = -1;    // 保存当前文字内容便签的ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_view_note);
        mScrollView.setBackgroundColor(PreferenceInfo.themeColorValue);
        mLinearLayout = (LinearLayout) findViewById(R.id.viewLayout);
        mLinearLayout.setBackgroundColor(PreferenceInfo.themeColorValue);

        mTitleView = (TextView) findViewById(R.id.mTitleText);
        mTitleView.setBackgroundColor(Color.parseColor("#ffffff"));
        mContentView = (TextView) findViewById(R.id.mContentText);
        mContentView.setBackgroundColor(PreferenceInfo.themeColorValue);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("noteBundle");
        mNote = bundle.getParcelable("note");
        mCurrentNoteId = mNote.getNoteId();
        Log.d(TAG, "onCreate:  id is "+mCurrentNoteId);
        mNoteDBAccess = new NoteDBAccess(this);
        mNote = mNoteDBAccess.selectNoteById(mCurrentNoteId);
        mTitleView.setText(mNote.getNoteTitle());
        mContentView.setText(mNote.getNoteContent());
    }

    @Override
    protected void onResume() {
        if (mBitmaps == null) {
            flush();
        }
        super.onResume();
    }

    /**
     * 刷新界面，动态添加ImageView控件，显示图片或者视频的缩略图
     */
    public void flush() {

        mMediaDBAccess = new MediaDBAccess(this);
        mMediaList = mMediaDBAccess.selectAll(mCurrentNoteId);
        mBitmaps = new ArrayList<>(3);
        mPathsList = new ArrayList<>(3);
        int len = mMediaList.size();
        String path;
        for (int i = 0; i < len; i++) {
            path = mMediaList.get(i).getPath();
            Log.d(TAG + "flush ", mMediaList.get(i).getDate().toString());  // 调试

            mPathsList.add(path);
            if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
                addThumbnail(path, i);

            }
        }
        for (int j = 0; j < len; j++) {
            path = mMediaList.get(j).getPath();
            if (path.endsWith(".mp4") || path.endsWith(".rmvb") || path.endsWith(".avi")) {
                addFileView(path, j, R.layout.add_video_file_layout);
            } else if (path.endsWith(".doc") || path.endsWith(".pdf")
                    || path.endsWith(".html") || path.endsWith(".txt") || path.endsWith(".ppt")) {
                addFileView(path, j, R.layout.add_text_file_layout);
                Log.d(TAG, "flush: " + path);

            } else if (path.endsWith(".mp3")) {
                addFileView(path, j, R.layout.add_audio_file_layout);
            }

        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int viewId = v.getId();
        if (mPathsList.get(viewId).endsWith(".jpg") || mPathsList.get(viewId).endsWith(".jpeg")
                || mPathsList.get(viewId).endsWith(".png")) {
            intent = new Intent(this, PhoneViewActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH, mPathsList.get(viewId));
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".mp4") || mPathsList.get(viewId).endsWith(".rmvb")
                || mPathsList.get(viewId).endsWith(".avi")) {
            intent = new Intent(this, VideoViewerActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH, mPathsList.get(viewId));
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".doc") || mPathsList.get(viewId).endsWith(".pdf")
                || mPathsList.get(viewId).endsWith(".ppt")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(mPathsList.get(viewId)));
            intent.setDataAndType(uri, "application/*");
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".html")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(mPathsList.get(viewId)));
            intent.setDataAndType(uri, "application/html");
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".mp3")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Uri uri = Uri.fromFile(new File(mPathsList.get(viewId)));
            intent.setDataAndType(uri, "audio/*");
            this.startActivity(intent);
        } else {
            Toast.makeText(this, "没有打开该类型文件的程序", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 动态添加视频或者图片缩略图视图
     */
    private void addThumbnail(String mediaPath, int id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mThumbnailLayout = (LinearLayout) layoutInflater.inflate(R.layout.add_image_file_layout, null);
        ImageView imageView = (ImageView) mThumbnailLayout.findViewById(R.id.mImageThumbnail);
        Bitmap bitmap = BitmapUtils.readBitMap(mediaPath, 4);
        mBitmaps.add(bitmap);
        imageView.setId(id);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(this);
        mLinearLayout.addView(mThumbnailLayout);
    }


    /**
     * 动态添加文本文件视图
     */
    private void addFileView(String path, int id, int layoutId) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mTextFileLayout = (LinearLayout) layoutInflater.inflate(layoutId, null);
        mTextFileLayout.setPadding(10, 10, 10, 10);
        TextView mTextView = (TextView) mTextFileLayout.findViewById(R.id.mTextView);
        mTextFileLayout.setId(id);
        mTextFileLayout.setOnClickListener(this);
        int index = path.lastIndexOf("/") + 1;
        String fileName = path.substring(index, path.length());
        mTextView.setText("  " + fileName);
        mLinearLayout.addView(mTextFileLayout);
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mBitmaps != null) {
            for (Bitmap bitmap : mBitmaps) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
        super.onDestroy();
    }

}
