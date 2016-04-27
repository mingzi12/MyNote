/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewNoteActivity extends Activity implements View.OnClickListener ,View.OnLongClickListener{

    private static final String TAG = "NewNoteActivity";
    private static final int SELECT_FILE_REQUEST_CODE = 3;
    private static final int SETTING_ALARM_REQUEST_CODE = 6;
    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private EditText mTitleEdit;
    private EditText mContentEdit;

    private boolean isTextChanged = true;
    private String mCurrentPath;
    private MediaDBAccess mMediaDBAccess;
    private int mCurrentNoteId = -1;
    private Date mDate;
    private List<String> mPathsList = null;
    private List<Bitmap> mBitmaps;
    private int mResultCode;
    private PreferenceInfo mPreferenceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTitle("返回");
        setContentView(R.layout.activity_edit);
        mPreferenceInfo =PreferenceInfo.getPreferenceInfo(this);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_edit);
        mScrollView.setBackgroundColor(mPreferenceInfo.themeColorValue);
        mLinearLayout = (LinearLayout) findViewById(R.id.editlayout);
        mLinearLayout.setBackgroundColor(mPreferenceInfo.themeColorValue);

        mTitleEdit = (EditText) findViewById(R.id.titleedit);
        mContentEdit = (EditText) findViewById(R.id.contentedit);
        mContentEdit.setBackgroundColor(mPreferenceInfo.themeColorValue);
        mContentEdit.requestFocus();
        mContentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanged) {
                    if (mTitleEdit.getText().toString().equals("")) {
                        if (mContentEdit.getText().toString().length() > 5) {
                            mTitleEdit.setText(mContentEdit.getText().subSequence(0, 5));
                        }
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mTitleEdit.getText().toString().equals(mContentEdit.getText().toString())) {
                    isTextChanged = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateOrNot();
    }


    public void updateOrNot() {
        Note note;
        NoteDBAccess access;
        String noteTitle = this.mTitleEdit.getText().toString();
        String noteContent = this.mContentEdit.getText().toString();
        if (mCurrentNoteId != -1) {  // mCurrentNoteId!=-1表示已插入一条空的文字便签但该便签附带有图片或者视频
            note = new Note();
            note.setNoteId(mCurrentNoteId);
            note.setCreateDate(mDate);
            note.setUpdateDate(mDate);
            access = new NoteDBAccess(NewNoteActivity.this);
            if (isTitleAndContentEmpty(noteTitle, noteContent)) { // 判断标题和正文是否同时为空
                note.setNoteTitle("无标题");
                note.setNoteContent("");
                access.updateNoteById(note);
                NewNoteActivity.this.finish();
            } else {
                if (noteTitle.equals("")) {
                    note.setNoteTitle("无标题");
                } else {
                    note.setNoteTitle(noteTitle);
                }
                note.setNoteContent(noteContent);
                access.updateNoteById(note);
                Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            if (isTitleAndContentEmpty(noteTitle, noteContent)) {
                NewNoteActivity.this.finish();
            } else {
                note = new Note();
                if (noteTitle.equals("")) {
                    note.setNoteTitle("无标题");
                } else {
                    note.setNoteTitle(noteTitle);
                }
                note.setNoteContent(noteContent);
                Date date = new Date();
                note.setCreateDate(date);
                note.setUpdateDate(date);
                access = new NoteDBAccess(this);
                access.insertNote(note);
                Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }

    /**
     * 添加菜单到ActionBar中
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_new, menu);

        return super.onCreateOptionsMenu(menu);
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
     * 响应ActionBar中的选项
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_new:
                if (mCurrentNoteId != -1) {
                    MediaDBAccess mediaDBAccess = new MediaDBAccess(this);
                    NoteDBAccess noteDBAccess = new NoteDBAccess(this);
                    mediaDBAccess.deleteById(mCurrentNoteId);
                    noteDBAccess.deleteNoteById(new Note(mCurrentNoteId));
                }
                NewNoteActivity.this.finish();
                break;

            case android.R.id.home:
                updateOrNot();
                break;

            case R.id.capture_video_new:
                if (mPathsList == null) {
                    mPathsList = new ArrayList<>(3);
                }
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
                startActivityForResult(videoIntent, ConstantValue.REQUEST_CODE_GET_VIDEO);
                break;
            case R.id.capture_img_new:
                if (mPathsList == null) {
                    mPathsList = new ArrayList<>(3);
                }
                if (mBitmaps == null) {
                    mBitmaps = new ArrayList<>(3);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imageFile = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
                if (!imageFile.exists()) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mCurrentPath = imageFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(intent, ConstantValue.REQUEST_CODE_GET_PHOTO);
                break;
            case R.id.send_new:
                shareMsg("分享到", mTitleEdit.getText().toString(), mContentEdit.getText().toString());
                break;

            case R.id.description_new:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("详细信息");
                builder.setMessage("创建时间 : " + ConvertStringAndDate.datetoString(new Date()) + "\n"
                        + "字数 : " + mContentEdit.getText().toString().length());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.alarm_new:
                if (mTitleEdit.getText().toString().equals("")&&mContentEdit.getText().toString().equals("")
                        &&mPathsList==null) {
                    Toast.makeText(this,"不能为空的便签设置闹钟",Toast.LENGTH_LONG).show();
                } else {
                    if (mCurrentNoteId==-1) {  //如果mCurrentNoteId为-1，则先插入一条记录
                        mDate = new Date();
                        Note note = new Note(mTitleEdit.getText().toString(),
                                mContentEdit.getText().toString(),mDate,mDate);
                        NoteDBAccess noteDBAccess= new NoteDBAccess(this);
                        mCurrentNoteId = noteDBAccess.insertNote(note);

                    } else {
                        mDate = new Date();
                    }
                        Note note = new Note(mCurrentNoteId,mTitleEdit.getText().toString(),
                                mContentEdit.getText().toString(),mDate,mDate);
                        Bundle lBundle = new Bundle();
                        lBundle.putParcelable(SetAlarmActivity.SETTING_ALARM, note);
                        Intent alarmIntent = new Intent(this, SetAlarmActivity.class);
                        alarmIntent.putExtra(SetAlarmActivity.SETTING_ALARM, lBundle);
                        startActivityForResult(alarmIntent, SETTING_ALARM_REQUEST_CODE);
                    }
                    Log.d(TAG, "onOptionsItemSelected: "+mCurrentNoteId);

                break;

            case R.id.add_extra_file_new:
                if (mPathsList == null) {
                    mPathsList = new ArrayList<>(3);
                }
                if (mBitmaps == null) {
                    mBitmaps = new ArrayList<>(3);
                }
                Intent selectFileIntent = new Intent(this, SelectFileActivity.class);
                selectFileIntent.putExtra("noteId", mCurrentNoteId);
                startActivityForResult(selectFileIntent, SELECT_FILE_REQUEST_CODE);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isTitleAndContentEmpty(String title, String content) {
        if (title.trim().equals("") && content.trim().equals("")) {
            return true;
        } else {
            return false;
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
     * 获取保存文件的路径
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantValue.REQUEST_CODE_GET_PHOTO: {
                if (resultCode == RESULT_OK) {
                    NoteDBAccess mNoteDBAccess = new NoteDBAccess(NewNoteActivity.this);
                    mMediaDBAccess = new MediaDBAccess(NewNoteActivity.this);
                    mDate = new Date();
                    if (mCurrentNoteId == -1) {
                        mCurrentNoteId = mNoteDBAccess.insertNullNote(new Note(mDate));
                    }
                    mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(mDate));
                    addView();
                } else if (resultCode == RESULT_CANCELED) {
                    File file = new File(mCurrentPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            break;
            case ConstantValue.REQUEST_CODE_GET_VIDEO: {
                if (resultCode == RESULT_OK) {
                    NoteDBAccess mNoteDBAccess = new NoteDBAccess(NewNoteActivity.this);
                    mMediaDBAccess = new MediaDBAccess(NewNoteActivity.this);
                    mDate = new Date();
                    if (mCurrentNoteId == -1) {
                        mCurrentNoteId = mNoteDBAccess.insertNullNote(new Note(mDate));
                    }
                    mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(mDate));
                    addView();
                } else if (resultCode == RESULT_CANCELED) {
                    File file = new File(mCurrentPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            break;
            case SELECT_FILE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {//有数据返回直接使用返回的图片地址
                        mCurrentPath = BitmapUtils.getFilePathByFileUri(this, data.getData());
                    }
                    if (mCurrentPath == null) {
                        mCurrentPath = Uri.decode(data.getDataString());
                        int len = mCurrentPath.length();
                        mCurrentPath = mCurrentPath.substring(7, len);
                    }
                    NoteDBAccess mNoteDBAccess = new NoteDBAccess(NewNoteActivity.this);
                    mMediaDBAccess = new MediaDBAccess(NewNoteActivity.this);
                    mDate = new Date();
                    if (mCurrentNoteId == -1) {
                        mCurrentNoteId = mNoteDBAccess.insertNullNote(new Note(mDate));
                    }
                    mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(mDate));
                    addView();
                }
            }
            break;
            case SETTING_ALARM_REQUEST_CODE:
                mResultCode = resultCode;
                Log.d(TAG, "onActivityResult: "+mResultCode);
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        int viewId = v.getId();
        if (mPathsList.get(viewId).endsWith(".jpg")||mPathsList.get(viewId).endsWith(".jpeg")
                ||mPathsList.get(viewId).endsWith(".png")) {
            intent = new Intent(this, PhoneViewActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH, mPathsList.get(viewId));
            startActivity(intent);
        } else if (mPathsList.get(viewId).endsWith(".mp4")||mPathsList.get(viewId).endsWith(".rmvb")
                ||mPathsList.get(viewId).endsWith(".avi")) {
            intent = new Intent(this, VideoViewerActivity.class);
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



    private void addView() {
        if (mCurrentPath.endsWith(".jpg") || mCurrentPath.endsWith("jpeg")
                || mCurrentPath.endsWith(".png")) {

            Log.d(TAG, "onActivityResult: " + mCurrentPath);
            mPathsList.add(mCurrentPath);

            addThumbnail(mCurrentPath, mPathsList.size() - 1);
        } else if (mCurrentPath.endsWith(".mp4") || mCurrentPath.endsWith(".rmvb")
                || mCurrentPath.endsWith(".avi")) {
            mPathsList.add(mCurrentPath);
            addFileView(mCurrentPath, mPathsList.size() - 1, R.layout.add_video_file_layout);
        } else if (mCurrentPath.endsWith(".doc") || mCurrentPath.endsWith(".pdf")
                || mCurrentPath.endsWith(".html") || mCurrentPath.endsWith(".txt") || mCurrentPath.endsWith(".ppt")) {
            mPathsList.add(mCurrentPath);

            addFileView(mCurrentPath, mPathsList.size() - 1, R.layout.add_text_file_layout);
        } else if (mCurrentPath.endsWith(".mp3")) {
            mPathsList.add(mCurrentPath);

            addFileView(mCurrentPath, mPathsList.size() - 1, R.layout.add_audio_file_layout);
        } else {
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
        layoutParams.setMargins(20, 10, 20, 10);
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
     * 动态添加视频或者图片缩略图视图
     * */
    private void addThumbnail(String mediaPath,int id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mThumbnailLayout = (LinearLayout) layoutInflater.inflate(R.layout.add_image_file_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,750);
        layoutParams.setMargins(20,10,20,10);
        mThumbnailLayout.setId(id);
        ImageView imageView = (ImageView) mThumbnailLayout.findViewById(R.id.mImageThumbnail);
        Bitmap bitmap = BitmapUtils.readBitMap(mediaPath, 2);
        mBitmaps.add(bitmap);
        imageView.setImageBitmap(bitmap);
        mThumbnailLayout.setOnClickListener(this);
        mThumbnailLayout.setOnLongClickListener(this);
        mLinearLayout.addView(mThumbnailLayout, layoutParams);
    }

    @Override
    public boolean onLongClick(View v) {
        final int viewId = v.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定要删除吗？")
                .setPositiveButton("删除文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MediaDBAccess(NewNoteActivity.this).deleteByPath(mPathsList.get(viewId));
                        NewNoteActivity.this.mLinearLayout.removeView(findViewById(viewId));
                        File file=new File(mPathsList.get(viewId));
                        if (file.exists()) {
                            file.delete();
                            Toast.makeText(NewNoteActivity.this, "文件已删除", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("取消附加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MediaDBAccess(NewNoteActivity.this).deleteByPath(mPathsList.get(viewId));
                        Toast.makeText(NewNoteActivity.this, "已取消附加文件", Toast.LENGTH_SHORT).show();
                        NewNoteActivity.this.mLinearLayout.removeView(findViewById(viewId));
                        dialog.dismiss();
                    }
                }).show();
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mBitmaps != null) {
            for (Bitmap bitmap :mBitmaps) {
                bitmap.recycle();
            }
        }
        super.onDestroy();
    }
}
