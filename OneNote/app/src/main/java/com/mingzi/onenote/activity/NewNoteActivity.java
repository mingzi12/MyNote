/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.DBAccess;
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
		setContentView(R.layout.edit);
		
		editLayout = (LinearLayout)findViewById(R.id.editlayout);
        editLayout.setBackgroundColor(PreferenceInfo.themeColorValue);
        
        noteTitleText = (EditText)findViewById(R.id.titleedit);
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
			
			DBAccess access = new DBAccess(this);
			access.insertNote(note);
			Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
	    	this.finish();
		}
	}
	
	private MenuItem menuItem_0;
	
	/**
	 * 响应手机菜单按钮
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuItem_0 = menu.add(0, 0, 0, "删除");
		menuItem_0.setIcon(R.drawable.delete_dark);
		menuItem_0.setOnMenuItemClickListener(new ItemClickListenerClass());
				
		return true;
	}
	
	/**
	 * 菜单按钮事件
	 */
	private class ItemClickListenerClass implements MenuItem.OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
				case 0: {
					NewNoteActivity.this.finish();
					break;
				}
			}
			return false;
		}
	}
}
