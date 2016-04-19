/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.ConvertStringAndDate;
import com.mingzi.onenote.util.MediaDBAccess;
import com.mingzi.onenote.util.BitmapUtils;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class NewNoteActivity extends Activity {

    private static final String TAG = "NewATY->";
    private static final int SELECT_FILE_REQUEST_CODE = 3;
    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private EditText mTitleEdit;
    private EditText mContentEdit;

    private boolean isTextChanged = true;
    private String mCurrentPath;
    private MediaDBAccess mMediaDBAccess;
    private int mCurrentNoteId = -1;
    private Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setTitle("返回");
        setContentView(R.layout.activity_edit);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_edit);
        mScrollView.setBackgroundColor(PreferenceInfo.themeColorValue);
        mLinearLayout = (LinearLayout) findViewById(R.id.editlayout);
        mLinearLayout.setBackgroundColor(PreferenceInfo.themeColorValue);

        mTitleEdit = (EditText) findViewById(R.id.titleedit);
        mTitleEdit.setBackgroundColor(Color.parseColor("#ffffff"));
        mContentEdit = (EditText) findViewById(R.id.contentedit);
        mContentEdit.setBackgroundColor(PreferenceInfo.themeColorValue);
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
                note.setCreateDate(mDate);
                note.setUpdateDate(mDate);
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
     * 响应ActionBar中的选项
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_new:
                NewNoteActivity.this.finish();
                break;

            case android.R.id.home:
                updateOrNot();
                break;

            case R.id.capture_video_new:
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

            case R.id.add_extra_file_new:
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
                    Log.d(TAG + "onResult ", mCurrentNoteId + ""); //调试
                    ImageView imageView = new ImageView(NewNoteActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    Bitmap bitmap = BitmapUtils.readBitMap(mCurrentPath, 4);
                    imageView.setImageBitmap(bitmap);
                    mLinearLayout.addView(imageView);

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
                    Log.d(TAG + "onResult ", mCurrentNoteId + ""); //调试
                    ImageView imageView = new ImageView(NewNoteActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    Bitmap bitmap = BitmapUtils.getVideoThumbnail(mCurrentPath, 900, 700,
                            MediaStore.Images.Thumbnails.MICRO_KIND);
                    imageView.setImageBitmap(bitmap);
                    mLinearLayout.addView(imageView);

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
                        NoteDBAccess mNoteDBAccess = new NoteDBAccess(NewNoteActivity.this);
                        mMediaDBAccess = new MediaDBAccess(NewNoteActivity.this);
                        mDate = new Date();
                        if (mCurrentNoteId == -1) {
                            mCurrentNoteId = mNoteDBAccess.insertNullNote(new Note(mDate));
                        }
                        mMediaDBAccess.insert(mCurrentPath, this.mCurrentNoteId, ConvertStringAndDate.datetoString(mDate));
                        Log.d(TAG + "onResult ", mCurrentNoteId + ""); //调试
                        ImageView imageView = new ImageView(NewNoteActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        imageView.setLayoutParams(layoutParams);
                        Bitmap bitmap = BitmapUtils.readBitMap(mCurrentPath, 4);
                        imageView.setImageBitmap(bitmap);
                        mLinearLayout.addView(imageView);
                    }

                }
                break;
            }

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
