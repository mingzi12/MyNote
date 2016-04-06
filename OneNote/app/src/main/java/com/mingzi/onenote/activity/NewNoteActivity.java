/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.util.Date;

public class NewNoteActivity extends Activity {
	
	private LinearLayout editLayout;
	private EditText noteTitleText;
	private EditText noteContentText;
	
	private boolean flagTextChanged = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("返回");
		setContentView(R.layout.edit);
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		editLayout = (LinearLayout)findViewById(R.id.editlayout);
        editLayout.setBackgroundColor(PreferenceInfo.themeColorValue);
        
        noteTitleText = (EditText)findViewById(R.id.titleedit);
		noteTitleText.setBackgroundColor(Color.parseColor("#ffffff"));
        noteContentText = (EditText)findViewById(R.id.contentedit);
		noteContentText.setBackgroundColor(PreferenceInfo.themeColorValue);
        noteContentText.requestFocus();
        noteContentText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if (flagTextChanged) {
					if (noteContentText.getText().toString().length()<3)
						noteTitleText.setText("");
					else
					noteTitleText.setText(noteContentText.getText().subSequence(0,5));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					if (!noteTitleText.getText().toString().equals(noteContentText.getText().toString())) {
					flagTextChanged = false;
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
		
		String noteTitle = noteTitleText.getText().toString();
		String noteContent = noteContentText.getText().toString();
		
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
		menuInflater.inflate(R.menu.new_menu,menu);
				
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
				String noteTitle = noteTitleText.getText().toString();
				String noteContent = noteContentText.getText().toString();

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
		}
		return super.onOptionsItemSelected(item);
	}


}
