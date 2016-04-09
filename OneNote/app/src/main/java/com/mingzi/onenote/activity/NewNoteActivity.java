/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
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
import com.mingzi.onenote.util.MyBitmap;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class NewNoteActivity extends Activity {

    private static final String TAG = "NewATY->";
    private ScrollView mScrollView;
	private LinearLayout mLinearLayout;
	private EditText noteTitle;
	private EditText noteContent;
	
	private boolean isTextChanged = true;
	private String currentPath;
    private MediaDBAccess mMediaDBAccess;
    private int currentNoteId = -1;
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
		mLinearLayout = (LinearLayout)findViewById(R.id.editlayout);
        mLinearLayout.setBackgroundColor(PreferenceInfo.themeColorValue);
        
        noteTitle = (EditText)findViewById(R.id.titleedit);
		noteTitle.setBackgroundColor(Color.parseColor("#ffffff"));
        noteContent = (EditText)findViewById(R.id.contentedit);
		noteContent.setBackgroundColor(PreferenceInfo.themeColorValue);
        noteContent.requestFocus();
        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanged) {
                    if (noteTitle.getText().toString().equals("")){
                        if (noteContent.getText().toString().length() > 5){
                            noteTitle.setText(noteContent.getText().subSequence(0, 5));
                        }
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!noteTitle.getText().toString().equals(noteContent.getText().toString())) {
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
        Note note;
        NoteDBAccess access;
        String noteTitle = this.noteTitle.getText().toString();
		String noteContent = this.noteContent.getText().toString();
		if (currentNoteId!=-1) {  // currentNoteId!=-1表示已插入一条空的文字便签但该便签附带有图片或者视频
            note = new Note();
            note.setNoteId(currentNoteId);
            note.setNoteDate(mDate);
            access = new NoteDBAccess(NewNoteActivity.this);
            if(isTitleAndContentEmpty(noteTitle,noteContent)) { // 判断标题和正文是否同时为空
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
                note.setNoteDate(mDate);
                access.updateNoteById(note);
                Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
        else {
            if(isTitleAndContentEmpty(noteTitle,noteContent)) {
                NewNoteActivity.this.finish();
            }
            else {
                 note = new Note();
                if (noteTitle.equals("")){
                    note.setNoteTitle("无标题");
                }
                else {
                    note.setNoteTitle(noteTitle);
                }
                note.setNoteContent(noteContent);
                note.setNoteDate(new Date());
                access = new NoteDBAccess(this);
                access.insertNote(note);
                Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }

	}


	/**
	 * 添加菜单到ActionBar中
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_new,menu);
				
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 响应ActionBar中的选项
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete_new :
				NewNoteActivity.this.finish();
				break;

			case android.R.id.home :
                NoteDBAccess access;
				String noteTitle = this.noteTitle.getText().toString();
				String noteContent = this.noteContent.getText().toString();
                if (currentNoteId != -1){
                    Note note = new Note();
                    note.setNoteId(currentNoteId);
                    note.setNoteDate(mDate);
                    access = new NoteDBAccess(NewNoteActivity.this);
                    if(isTitleAndContentEmpty(noteTitle,noteContent)) {
                        note.setNoteTitle("无标题");
                        note.setNoteContent("");
                        access.updateNoteById(note);
                        NewNoteActivity.this.finish();
                    } else {
                        if (noteTitle.equals("")){
                            note.setNoteTitle("无标题");
                        } else {
                            note.setNoteTitle(noteTitle);
                        }
                        note.setNoteContent(noteContent);
                        note.setNoteDate(mDate);
                        access.updateNoteById(note);
                        Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
                        this.finish();
                    }
                }
				else {
                    Note note = new Note();
                    if (noteTitle.equals("")){
                        note.setNoteTitle("无标题");
                    }
                    else {
                        note.setNoteTitle(noteTitle);
                    }
                    note.setNoteContent(noteContent);
                    note.setNoteDate(new Date());
                    access = new NoteDBAccess(this);
                    access.insertNote(note);
                    Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
                    this.finish();
                }
				break;

            case R.id.capture_video_new :
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File videoFile = new File(getMediaDir(),System.currentTimeMillis()+".mp4");
                if (!videoFile.exists()){
                    try {
                        videoFile.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                currentPath = videoFile.getAbsolutePath();
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                startActivityForResult(videoIntent,ConstantValue.REQUEST_CODE_GET_VIDEO);
                break;
            case R.id.capture_img_new :
                Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imageFile = new File(getMediaDir(),System.currentTimeMillis()+".jpg");
                if (!imageFile.exists()){
                    try {
                        imageFile.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                currentPath = imageFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(intent, ConstantValue.REQUEST_CODE_GET_PHOTO);
                break;
            case R.id.send_new :
                break;
            default :
                break;
		}
		return super.onOptionsItemSelected(item);
	}

    private boolean isTitleAndContentEmpty(String title, String content){
        if (title.trim().equals("")&&content.trim().equals("")){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 获取保存文件的路径
     * */
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
            case ConstantValue.REQUEST_CODE_GET_PHOTO :
                if (resultCode == RESULT_OK) {
                    NoteDBAccess mNoteDBAccess = new NoteDBAccess(NewNoteActivity.this);
                    mMediaDBAccess = new MediaDBAccess(NewNoteActivity.this);
                    mDate = new Date();
                    currentNoteId = mNoteDBAccess.insertNullNote(new Note(mDate));
                    mMediaDBAccess.insert(currentPath, this.currentNoteId, ConvertStringAndDate.datetoString(mDate));
                    Log.d(TAG + "onResult ", currentNoteId + ""); //调试
                    ImageView imageView = new ImageView(NewNoteActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    Bitmap bitmap = MyBitmap.readBitMap(currentPath, 4);
                    imageView.setImageBitmap(bitmap);
                    mLinearLayout.addView(imageView);

                }
                else if (resultCode == RESULT_CANCELED) {
                    File file = new File(currentPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                break;
            case ConstantValue.REQUEST_CODE_GET_VIDEO :
                if (resultCode == RESULT_OK) {
                    NoteDBAccess mNoteDBAccess = new NoteDBAccess(NewNoteActivity.this);
                    mMediaDBAccess = new MediaDBAccess(NewNoteActivity.this);
                    mDate = new Date();
                    currentNoteId = mNoteDBAccess.insertNullNote(new Note(mDate));
                    mMediaDBAccess.insert(currentPath, this.currentNoteId, ConvertStringAndDate.datetoString(mDate));
                    Log.d(TAG + "onResult ", currentNoteId + ""); //调试
                    ImageView imageView = new ImageView(NewNoteActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    Bitmap bitmap = MyBitmap.getVideoThumbnail(currentPath, 900, 700,
                            MediaStore.Images.Thumbnails.MICRO_KIND);
                    imageView.setImageBitmap(bitmap);
                    mLinearLayout.addView(imageView);

                }
                else if (resultCode == RESULT_CANCELED) {
                    File file = new File(currentPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
