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
import java.util.List;

public class NewNoteActivity extends Activity {

    private static final String TAG = "NewATY->";
    private ScrollView mScrollView;
	private LinearLayout mLinearLayout;
	private EditText noteTitle;
	private EditText noteContent;
	
	private boolean isTextChanged = true;
	private String currentPath;
    private MediaDBAccess mMediaDBAccess;
    private int currentNoteId;
    private List<Bitmap> mBitmapList;


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
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		String noteTitle = this.noteTitle.getText().toString();
		String noteContent = this.noteContent.getText().toString();
		
		if(noteTitle.toString().trim().equals("") && noteContent.toString().trim().equals("")) {
			NewNoteActivity.this.finish();
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
			
			NoteDBAccess access = new NoteDBAccess(this);
			access.insertNote(note);
			Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
	    	this.finish();
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
        Intent intent;
        File mediaFile;
		switch (item.getItemId()) {
			case R.id.delete_new :
				NewNoteActivity.this.finish();
				break;

			case android.R.id.home :
				String noteTitle = this.noteTitle.getText().toString();
				String noteContent = this.noteContent.getText().toString();

				if(noteTitle.toString().trim().equals("") && noteContent.toString().trim().equals("")) {
					NewNoteActivity.this.finish();
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

					NoteDBAccess access = new NoteDBAccess(this);
					access.insertNote(note);
					Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
					this.finish();
				}
				break;
            case R.id.capture_video_new :
                break;
            case R.id.capture_img_new :
                intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mediaFile = new File(getMediaDir(),System.currentTimeMillis()+".jpg");
                if (!mediaFile.exists()){
                    try {
                        mediaFile.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                currentPath = mediaFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                startActivityForResult(intent, ConstantValue.REQUEST_CODE_GET_PHOTO);
                break;
            case R.id.send_new :
                break;
            default :
                break;
		}
		return super.onOptionsItemSelected(item);
	}

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
                    Date date = new Date();
                    currentNoteId = mNoteDBAccess.insertNullNote(new Note(date));
                    mMediaDBAccess.insert(currentPath, this.currentNoteId, ConvertStringAndDate.datetoString(date));
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
                    if (file.exists()&&file.length()==0) {
                        file.delete();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
