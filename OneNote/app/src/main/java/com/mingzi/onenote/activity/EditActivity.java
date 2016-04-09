package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.ConvertStringAndDate;
import com.mingzi.onenote.util.MediaDBAccess;
import com.mingzi.onenote.util.MyBitmap;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Media;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditActivity extends Activity implements ImageView.OnClickListener {

    public static final String TAG = "EditATY-> ";
    private ScrollView mScrollView;
	private LinearLayout mLinearLayout;
	private EditText noteTitle;
	private EditText noteContent;
	private int titleLength;     // 保存初始Title的长度，用于判断Title是否被修改
	private int contentLength;   // 保存内容的初始长度，用于判断内容是否变化
	private Note note;

    private MediaDBAccess mMediaDBAccess;
    private List<Media> mMediaList;

    private List<Bitmap> mBitmaps;
    private List<String> paths;        // 存放和当前便签匹配的所有图片或者视频的路径
    private int currentNoteId = -1;    // 保存当前文字内容便签的ID
    private String currentPath = null; // 保存当前图片或者视频的路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("返回");
		setContentView(R.layout.activity_edit);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_edit);
        mScrollView.setBackgroundColor(PreferenceInfo.themeColorValue);
		mLinearLayout = (LinearLayout)findViewById(R.id.editlayout);
        mLinearLayout.setBackgroundColor(PreferenceInfo.themeColorValue);

        noteTitle = (EditText)findViewById(R.id.titleedit);
		noteTitle.setBackgroundColor(Color.parseColor("#ffffff"));
        noteContent = (EditText)findViewById(R.id.contentedit);
		noteContent.setBackgroundColor(PreferenceInfo.themeColorValue);


		Intent intent = this.getIntent();
	    Bundle bundle = intent.getBundleExtra("noteBundle");
	    note = (Note)bundle.getParcelable("note");
        currentNoteId = note.getNoteId();
        Log.d(TAG+ "onCreate", currentNoteId +"");
	    contentLength = note.getNoteContent().length();
		titleLength = note.getNoteTitle().length();
	    noteTitle.setText(note.getNoteTitle());
	    noteContent.setText(note.getNoteContent());
		noteContent.setSelection(noteContent.getText().length());

    }

    @Override
    protected void onResume() {
        if (mBitmaps==null){
            flush();
        }
        super.onResume();
    }

    public void flush(){

        mMediaDBAccess = new MediaDBAccess(EditActivity.this);
        mMediaList = mMediaDBAccess.selectAll(currentNoteId);
        mBitmaps = new ArrayList<>();
        paths = new ArrayList<>();
        int len = mMediaList.size();
        String path;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i=0;i<len;i++){
            path = mMediaList.get(i).getPath();
            Log.d(TAG + "flush ", mMediaList.get(i).getDate().toString());  // 调试

            paths.add(path);
            if (path.endsWith(".jpg")){
                Bitmap bitmap = MyBitmap.readBitMap(path,4);
                mBitmaps.add(bitmap);
                ImageView imageView = new ImageView(EditActivity.this);
                imageView.setId(i);
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(this);
                mLinearLayout.addView(imageView);
            }
            else if (path.endsWith(".mp4")){
                Bitmap bitmap = MyBitmap.getVideoThumbnail(path, 900, 400, MediaStore.Images.Thumbnails.MICRO_KIND);
                mBitmaps.add(bitmap);
                Log.d(TAG + "flush ", path);  // 调试
                ImageView imageView = new ImageView(EditActivity.this);
                imageView.setId(i);
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(this);
                mLinearLayout.addView(imageView);
            }

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int viewId = v.getId();
        if (paths.get(viewId).endsWith(".jpg")){
            intent= new Intent(EditActivity.this,PhoneViewActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH,paths.get(viewId));
            startActivity(intent);
        }
        else if (paths.get(viewId).endsWith(".mp4")){
            intent= new Intent(EditActivity.this,VideoViewerActivity.class);
            intent.putExtra(PhoneViewActivity.EXTRA_PATH,paths.get(viewId));
            startActivity(intent);
        }
    }

    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (noteContent.getText().toString().length()!=contentLength || noteTitle.getText().length()!=titleLength){
			if (noteTitle.getText().length()==0){
				note.setNoteTitle("无标题");
			}
			else {
				note.setNoteTitle(noteTitle.getText().toString());
			}
			String noteContent = this.noteContent.getText().toString();
			note.setNoteContent(noteContent);
			note.setNoteDate(new Date());

			NoteDBAccess access = new NoteDBAccess(this);
			access.updateNote(note);

			Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		else
			this.finish();
		
	}
	

	/**
	 * 响应手机菜单按钮
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
        Intent intent;
        File mediaFile;
		switch (item.getItemId()) {
			case R.id.delete_edit :
				AlertDialog.Builder builder = new Builder(EditActivity.this);
				builder.setTitle("删除");
				builder.setIcon(R.drawable.delete_light);
				builder.setMessage("您确定要把日志删除吗？");
				builder.setPositiveButton("确定",new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						NoteDBAccess access = new NoteDBAccess(EditActivity.this);
						access.deleteNoteById(note);
                        mMediaDBAccess.deleteById(currentNoteId);
                        /*
                        * 开启一个线程删除本地图片或者视频
                        * */
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                File file;
                                for (String fileStr : paths){
                                    file = new File(fileStr);
                                    if (file.exists()){
                                        file.delete();
                                    }
                                }
                            }
                        }.start();
						dialog.dismiss();
						Toast.makeText(EditActivity.this, "已删除", Toast.LENGTH_LONG).show();
						EditActivity.this.finish();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
				break;

			case R.id.send_edit :
				Intent iIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));

				if (!note.getNoteContent().equals(note.getNoteTitle())) {
					iIntent.putExtra("sms_body", note.getNoteTitle() + "\n" + note.getNoteContent());
				}
				else {
					iIntent.putExtra("sms_body", note.getNoteContent());
				}
				EditActivity.this.startActivity(iIntent);
				break;

			case  android.R.id.home :
				if (noteContent.getText().toString().length()!=contentLength ||
						noteTitle.getText().length()!=titleLength) {
					if (noteTitle.getText().length() == 0) {
						note.setNoteTitle("无标题");
					} else {
						note.setNoteTitle(noteTitle.getText().toString());
					}
					String noteContent = this.noteContent.getText().toString();
					note.setNoteContent(noteContent);
					note.setNoteDate(new Date());

					NoteDBAccess access = new NoteDBAccess(this);
					access.updateNote(note);

					Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show();
				}
				finish();
				break;

            case R.id.capture_img_edit :
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(mediaFile));
                startActivityForResult(intent, ConstantValue.REQUEST_CODE_GET_PHOTO);
                break;

            case R.id.add_video_edit :
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                mediaFile = new File(getMediaDir(),System.currentTimeMillis()+".mp4");
                if (!mediaFile.exists()){
                    try {
                        mediaFile.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                currentPath = mediaFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(mediaFile));
                startActivityForResult(intent,ConstantValue.REQUEST_CODE_GET_VIDEO);
                break;

			default :
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	* 显示ActionBar中每个子项的图标
	* */
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantValue.REQUEST_CODE_GET_PHOTO :
                if (resultCode == RESULT_OK) {
                    mMediaDBAccess.insert(currentPath, this.currentNoteId, ConvertStringAndDate.datetoString(new Date()));
                    paths.add(currentPath);
                    Log.d(TAG + "onResult ", currentNoteId + ""); //调试
                    ImageView imageView = new ImageView(EditActivity.this);
                    imageView.setId(paths.size() - 1);
                    imageView.setOnClickListener(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    Bitmap bitmap = MyBitmap.readBitMap(currentPath,4);
                    mBitmaps.add(bitmap);
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

            case ConstantValue.REQUEST_CODE_GET_VIDEO :
                if (resultCode == RESULT_OK) {
                    mMediaDBAccess.insert(currentPath, this.currentNoteId, ConvertStringAndDate.datetoString(new Date()));
                    paths.add(currentPath);
                    Log.d(TAG + "onResult ", currentNoteId + ""); //调试
                    ImageView imageView = new ImageView(EditActivity.this);
                    imageView.setId(paths.size() - 1);
                    imageView.setOnClickListener(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    Bitmap bitmap = MyBitmap.getVideoThumbnail(currentPath, 900, 600, MediaStore.Images.Thumbnails.MICRO_KIND);
                    mBitmaps.add(bitmap);
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
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mBitmaps!=null){
            for (Bitmap bitmap : mBitmaps){
                if (bitmap!=null){
                    bitmap.recycle();
                }
            }
        }
        super.onDestroy();
    }
}
