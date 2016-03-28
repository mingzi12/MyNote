package com.mingzi.onenote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.DBAccess;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.util.Date;

public class EditActivity extends Activity {
	
	private LinearLayout editLayout;
	private EditText noteTitleText;
	private EditText noteContentText;
	private int titleLength; //保存初始Title的长度，用于判断Title是否被修改
	private int contentLength; // 保存内容的初始长度，用于判断内容是否变化
	private Note note;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		
		editLayout = (LinearLayout)findViewById(R.id.editlayout);
        editLayout.setBackgroundColor(PreferenceInfo.themeColorValue);

        noteTitleText = (EditText)findViewById(R.id.titleedit);
		noteTitleText.setBackgroundColor(Color.parseColor("#ffffff"));
        noteContentText = (EditText)findViewById(R.id.contentedit);
		noteContentText.setBackgroundColor(PreferenceInfo.themeColorValue);


		Intent intent = this.getIntent();
	    Bundle bundle = intent.getBundleExtra("noteBundle");
	    note = (Note)bundle.getParcelable("note");
	    contentLength = note.getNoteContent().length();
		titleLength = note.getNoteTitle().length();
	    noteTitleText.setText(note.getNoteTitle());
	    noteContentText.setText(note.getNoteContent());
		noteContentText.setSelection(noteContentText.getText().length());
		noteContentText.requestFocus();
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (noteContentText.getText().toString().length()!=contentLength || noteTitleText.getText().length()!=titleLength){
			if (noteTitleText.getText().length()==0){
				note.setNoteTitle("无标题");
			}
			else {
				note.setNoteTitle(noteTitleText.getText().toString());
			}
			String noteContent = noteContentText.getText().toString();
			note.setNoteContent(noteContent);
			note.setNoteDate(new Date());

			DBAccess access = new DBAccess(this);
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
		menuInflater.inflate(R.menu.menu_edit,menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete_edit :
				AlertDialog.Builder builder = new Builder(EditActivity.this);
				builder.setTitle("删除");
				builder.setIcon(R.drawable.delete_light);
				builder.setMessage("您确定要把日志删除吗？");
				builder.setPositiveButton("确定",new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						DBAccess access = new DBAccess(EditActivity.this);
						access.deleteNote(note);

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
			default :
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	
}
