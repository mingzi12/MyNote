package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.BitmapUtils;
import com.mingzi.onenote.util.ConvertStringAndDate;
import com.mingzi.onenote.util.MediaDBAccess;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Media;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditActivity extends Activity implements ImageView.OnClickListener ,View.OnLongClickListener {

    public static final String TAG = "EditATY-> ";

    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
    private static final int CAPTURE_VIDEO_REQUEST_CODE = 2;
    private static final int SET_ALARM_REQUEST_CODE = 3;
    private static final int RECORD_AUDIO_REQUESt_CODE = 4;

    /**
     * 选择文件的请求码
     */
    private final static int SELECT_FILE_REQUEST_CODE = 300;

    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private EditText mTitleEdit;
    private EditText mContentEdit;
    private String mTitle;     // 保存初始Title的长度，用于判断Title是否被修改
    private String mContent;   // 保存内容的初始长度，用于判断内容是否变化
    private Note mNote;

    private MediaDBAccess mMediaDBAccess;
    private List<Media> mMediaList;

    private List<Bitmap> mBitmaps;
    private List<String> mPathsList;        // 存放和当前便签匹配的所有图片或者视频的路径
    private int mCurrentNoteId = -1;    // 保存当前文字内容便签的ID
    private String mCurrentPath = null; // 保存当前图片或者视频的路径
    private PreferenceInfo mPreferenceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTitle("   返回");
        setContentView(R.layout.activity_edit);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mPreferenceInfo = PreferenceInfo.getPreferenceInfo(this);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_edit);
        mScrollView.setBackgroundColor(mPreferenceInfo.themeColorValue);
        mLinearLayout = (LinearLayout) findViewById(R.id.editlayout);
        mLinearLayout.setBackgroundColor(mPreferenceInfo.themeColorValue);

        mTitleEdit = (EditText) findViewById(R.id.titleedit);
        mContentEdit = (EditText) findViewById(R.id.contentedit);
        mContentEdit.setBackgroundColor(mPreferenceInfo.themeColorValue);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("noteBundle");
        mNote = bundle.getParcelable("note");
        mCurrentNoteId = mNote.getNoteId();
        Log.d(TAG + "onCreate", mCurrentNoteId + "");
        mContent = mNote.getNoteContent();
        mTitle = mNote.getNoteTitle();
        mTitleEdit.setText(mNote.getNoteTitle());
        mContentEdit.setText(mNote.getNoteContent());
        mContentEdit.setSelection(mContentEdit.getText().length());
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

        mMediaDBAccess = new MediaDBAccess(EditActivity.this);
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
            for (int j = 0;j< len; j++) {
                path = mMediaList.get(j).getPath();
                if (path.endsWith(".mp4")||path.endsWith(".rmvb")||path.endsWith(".avi")) {
                    addFileView(path, j, R.layout.add_video_file_layout);
                } else if (path.endsWith(".doc")||path.endsWith(".pdf")
                        ||path.endsWith(".html")||path.endsWith(".txt")||path.endsWith(".ppt")) {
                    addFileView(path,j,R.layout.add_text_file_layout);
                    Log.d(TAG, "flush: "+path);

                } else if (path.endsWith(".mp3")) {
                    addFileView(path,j,R.layout.add_audio_file_layout);
                }

            }

    }

    /**
     * 动态添加视频或者图片缩略图视图
     * */
    private void addThumbnail(String mediaPath,int id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mThumbnailLayout = (LinearLayout) layoutInflater.inflate(R.layout.add_image_file_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,650);
        layoutParams.setMargins(20,15,20,15);
        mThumbnailLayout.setId(id);
        ImageView imageView = (ImageView) mThumbnailLayout.findViewById(R.id.mImageThumbnail);
        Bitmap bitmap = BitmapUtils.readBitMap(mediaPath, 2);
        mBitmaps.add(bitmap);
        imageView.setImageBitmap(bitmap);
        mThumbnailLayout.setOnClickListener(this);
        mThumbnailLayout.setOnLongClickListener(this);
        mLinearLayout.addView(mThumbnailLayout,layoutParams);
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        int viewId = v.getId();
        if (mPathsList.get(viewId).endsWith(".jpg")||mPathsList.get(viewId).endsWith(".jpeg")
                ||mPathsList.get(viewId).endsWith(".png")) {
            intent = new Intent(EditActivity.this, PhoneViewActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH, mPathsList.get(viewId));
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".mp4")||mPathsList.get(viewId).endsWith(".rmvb")
                ||mPathsList.get(viewId).endsWith(".avi")) {
            intent = new Intent(EditActivity.this, VideoViewerActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH, mPathsList.get(viewId));
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".doc")||mPathsList.get(viewId).endsWith(".pdf")
                ||mPathsList.get(viewId).endsWith(".ppt")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(mPathsList.get(viewId)));
            intent.setDataAndType(uri, "application/*");
            startActivity(intent);
        }
        else if (mPathsList.get(viewId).endsWith(".html")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(mPathsList.get(viewId)));
            intent.setDataAndType(uri, "application/html");
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".mp3")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Uri uri = Uri.fromFile(new File(mPathsList.get(viewId)));
            intent.setDataAndType (uri, "audio/*");
            this.startActivity(intent);
        }
        else {
            Toast.makeText(this,"没有打开该类型文件的程序",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final int viewId = v.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定要删除吗？")
                .setPositiveButton("删除文件", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MediaDBAccess(EditActivity.this).deleteByPath(mPathsList.get(viewId));
                        EditActivity.this.mLinearLayout.removeView(findViewById(viewId));
                        File file=new File(mPathsList.get(viewId));
                        if (file.exists()) {
                            file.delete();
                            Toast.makeText(EditActivity.this, "文件已删除", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("取消附加", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MediaDBAccess(EditActivity.this).deleteByPath(mPathsList.get(viewId));
                        Toast.makeText(EditActivity.this, "已取消附加文件", Toast.LENGTH_SHORT).show();
                        EditActivity.this.mLinearLayout.removeView(findViewById(viewId));
                        dialog.dismiss();
                    }
                }).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        updateOrNot();
        this.finish();

    }


    /**
     * 响应手机菜单按钮
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_edit:
                AlertDialog.Builder builder = new Builder(EditActivity.this);
                builder.setTitle("    删除");
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage("您确定要把日志删除吗？");
                builder.setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NoteDBAccess access = new NoteDBAccess(EditActivity.this);
                        access.deleteNoteById(mNote);
                        mMediaDBAccess.deleteById(mCurrentNoteId);
                        /*
                        * 开启一个线程删除本地图片或者视频
                        * */
                       /* new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                File file;
                                for (String fileStr : mPathsList) {
                                    file = new File(fileStr);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                            }
                        }.start();*/

                        dialog.dismiss();
                        Toast.makeText(EditActivity.this, "已删除", Toast.LENGTH_LONG).show();
                        EditActivity.this.finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;

            case R.id.send_edit:
                shareMsg("分享到", mTitleEdit.getText().toString(), mContentEdit.getText().toString());
                break;

            case android.R.id.home:
                updateOrNot();
                finish();
                break;

            case R.id.capture_img_edit:
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imageFile = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
                if (!imageFile.exists()) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mCurrentPath = imageFile.getAbsolutePath();
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
                break;

            case R.id.capture_video_edit:
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File videoFile = new File(getMediaDir(), System.currentTimeMillis() + ".mp4");
                if (!videoFile.exists()) {
                    try {
                        videoFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mCurrentPath = videoFile.getAbsolutePath();
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                startActivityForResult(videoIntent,CAPTURE_VIDEO_REQUEST_CODE);
                break;
            case R.id.description_edit:
                final AlertDialog.Builder descBuilder = new Builder(EditActivity.this);
                descBuilder.setTitle("详细信息");
                if (mNote.getCreateDate().compareTo(mNote.getUpdateDate()) == 0) {
                    descBuilder.setMessage("创建时间 : " + ConvertStringAndDate.datetoString(mNote.getCreateDate())
                            + "\n" + "修改时间 : " + ConvertStringAndDate.datetoString(mNote.getCreateDate()) + "\n"
                            + "字数 : " + mNote.getNoteContent().length());
                } else {
                    descBuilder.setMessage("创建时间 : " + ConvertStringAndDate.datetoString(mNote.getCreateDate())
                            + "\n" + "修改时间 : " + ConvertStringAndDate.datetoString(mNote.getUpdateDate()) + "\n"
                            + "字数 : " + mNote.getNoteContent().length());
                }
                descBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                descBuilder.create().show();
                break;

            case R.id.alarm_edit:
                Bundle lBundle = new Bundle();
                lBundle.putParcelable(SetAlarmActivity.SETTING_ALARM, mNote);
                Intent alarmIntent = new Intent(EditActivity.this, SetAlarmActivity.class);
                alarmIntent.putExtra(SetAlarmActivity.SETTING_ALARM, lBundle);
                startActivityForResult(alarmIntent, SET_ALARM_REQUEST_CODE);
                break;
            case R.id.extra_file_edit:
                Intent selectFileIntent = new Intent(this, SelectFileActivity.class);
                startActivityForResult(selectFileIntent, SELECT_FILE_REQUEST_CODE);
                break;
            case R.id.record_audio_edit:
                Intent recordIntent = new Intent(Intent.ACTION_GET_CONTENT);
                recordIntent.setType("audio/*");
                startActivityForResult(recordIntent,RECORD_AUDIO_REQUESt_CODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 是否更新当前便签
     */

    public void updateOrNot() {
        if (!mContentEdit.getText().toString().equals(mContent) || !mTitleEdit.getText().toString().equals(mTitle)) {
            if (mTitleEdit.getText().length() == 0) {
                mNote.setNoteTitle("无标题");
            } else {
                mNote.setNoteTitle(mTitleEdit.getText().toString());
            }
            String noteContent = this.mContentEdit.getText().toString();
            mNote.setNoteContent(noteContent);
            mNote.setCreateDate(new Date());
            NoteDBAccess access = new NoteDBAccess(this);
            access.updateNoteById(mNote);
            Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享
     */

    public void shareMsg(String activityTitle, String msgTitle, String msgContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgContent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    /**
     * 显示ActionBar中每个子项的图标
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 根据resultCode的返回值，
     * 做相应处理
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    addView();

                } else if (resultCode == RESULT_CANCELED) {
                    File file = new File(mCurrentPath);
                    if (file.exists() && file.length() == 0) {
                        file.delete();
                    }
                }
                break;

            case CAPTURE_VIDEO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG + "onResult ", mCurrentNoteId + ""); //调试
                    addView();
                } else if (resultCode == RESULT_CANCELED) {
                    File file = new File(mCurrentPath);
                    if (file.exists() && file.length() == 0) {
                        file.delete();
                    }
                }
                break;

            case SELECT_FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {//有数据返回直接使用返回的图片地址
                        mCurrentPath = BitmapUtils.getFilePathByFileUri(this, data.getData());
                    }
                    if (mCurrentPath==null) {
                        mCurrentPath = Uri.decode(data.getDataString());
                        int len = mCurrentPath.length();
                        mCurrentPath = mCurrentPath.substring(7,len);
                    }
                    Log.d(TAG, "onActivityResult: " + mCurrentPath);
                    addView();
                }

                break;
            case RECORD_AUDIO_REQUESt_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {//有数据返回直接使用返回的图片地址
                        mCurrentPath = BitmapUtils.getFilePathByFileUri(this, data.getData());
                    }
                    if (mCurrentPath==null) {
                        mCurrentPath = Uri.decode(data.getDataString());
                        int len = mCurrentPath.length();
                        mCurrentPath = mCurrentPath.substring(7,len);
                    }
                    Log.d(TAG, "onActivityResult: " + mCurrentPath);
                    addView();
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addView() {
        Log.d(TAG + "onResult ", mCurrentNoteId + ""); //调试
        if (mCurrentPath.endsWith(".jpg") || mCurrentPath.endsWith("jpeg")
                || mCurrentPath.endsWith(".png")) {
            mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(new Date()));
            Log.d(TAG, "onActivityResult: " + mCurrentPath);
            mPathsList.add(mCurrentPath);
           addThumbnail(mCurrentPath,mPathsList.size()-1);
        } else if (mCurrentPath.endsWith(".mp4") || mCurrentPath.endsWith(".rmvb")
                || mCurrentPath.endsWith(".avi")) {
            mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(new Date()));
            mPathsList.add(mCurrentPath);
            addFileView(mCurrentPath,mPathsList.size()-1,R.layout.add_video_file_layout);
        } else if (mCurrentPath.endsWith(".doc")||mCurrentPath.endsWith(".pdf")
                ||mCurrentPath.endsWith(".html")||mCurrentPath.endsWith(".txt")||mCurrentPath.endsWith(".ppt")) {
            mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(new Date()));
            mPathsList.add(mCurrentPath);

            addFileView(mCurrentPath,mPathsList.size() - 1,R.layout.add_text_file_layout);
        } else if (mCurrentPath.endsWith(".mp3")) {
            mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(new Date()));
            mPathsList.add(mCurrentPath);

            addFileView(mCurrentPath,mPathsList.size() - 1,R.layout.add_audio_file_layout);
        }

        else {
            Toast.makeText(this, "不支持添加该类型的文件", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 动态添加文本文件视图
     * */
    private void addFileView(String path ,int id , int layoutId) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mTextFileLayout = (LinearLayout) layoutInflater.inflate(layoutId, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
        layoutParams.setMargins(20,10,20,10);
        TextView mTextView = (TextView) mTextFileLayout.findViewById(R.id.mTextView);
        mTextFileLayout.setId(id);
        mTextFileLayout.setOnClickListener(this);
        mTextFileLayout.setOnLongClickListener(this);
        int index = path.lastIndexOf("/")+1;
        String fileName = path.substring(index,path.length());
        mTextView.setText("  "+fileName);
        mLinearLayout.addView(mTextFileLayout,layoutParams);
    }

    /**
     * 获取存放文件的文件名
     */
    public File getMediaDir() {
        File dir = new File(Environment.getExternalStorageDirectory(),
                "OneNote");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {

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
