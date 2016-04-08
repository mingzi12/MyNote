/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.adapter.NoteBaseAdapter;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.util.ArrayList;
import java.util.List;



/**
 *
 */
public class MainActivity extends Activity {

    public static final String TAG = "EditActivity ----> ";

	private TextView noteNumTextView;
	private ListView noteListView;
	private ImageView imageViewAdd, imageViewSearch;
	private MenuItem menuItem_0, menuItem_1;

	private NoteBaseAdapter noteBaseAdapter;

	private NoteDBAccess access;
	private List<Note> mNoteList;
	private Note note = new Note();
	private PreferenceInfo mPreferenceInfo;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        noteNumTextView = (TextView)findViewById(R.id.numtext);
        
        imageViewAdd = (ImageView)findViewById(R.id.addbutton);
		imageViewAdd.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, NewNoteActivity.class);
				MainActivity.this.startActivity(intent);
				
				return false;
			}
		});
		
		imageViewSearch = (ImageView)findViewById(R.id.searchbutton);
		imageViewSearch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchActivity.class);
				MainActivity.this.startActivity(intent);
				return false;
			}
		});
		
		noteListView = (ListView)findViewById(R.id.notelist);
		noteListView.setOnItemClickListener(new OnItemSelectedListener());
		mNoteList = new ArrayList<Note>();
		access = new NoteDBAccess(this);
		mPreferenceInfo = new PreferenceInfo(this);

		this.registerForContextMenu(noteListView);
    }
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (PreferenceInfo.ifLocked) {
			final EditText keytext = new EditText(MainActivity.this);
			keytext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("请输入密码");
			builder.setIcon(R.drawable.lock_light);
			builder.setView(keytext);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (PreferenceInfo.userPasswordValue.equals(keytext.getText().toString())) {
						PreferenceInfo.appLock(false);
						Toast.makeText(MainActivity.this, "已解除锁定", Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_LONG).show();
						MainActivity.this.finish();
					}
				}
			});
			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					MainActivity.this.finish();
				}
			});
			builder.create().show();
		}

		flush();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menuItem_0 = menu.add(0, 0, 0, "设置");
		menuItem_0.setIcon(R.drawable.setting_dark);
		menuItem_0.setOnMenuItemClickListener(new ItemClickListtenerClass());

		menuItem_1 = menu.add(0, 1, 1, "锁定");
		menuItem_1.setIcon(R.drawable.lock_dark);
		menuItem_1.setOnMenuItemClickListener(new ItemClickListtenerClass());

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 *准备菜单按钮
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menuItem_1.setVisible(!PreferenceInfo.userPasswordValue.equals(""));

		return super.onPrepareOptionsMenu(menu);
	}

    private class ItemClickListtenerClass implements MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
			case 0:{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, OneNotePreferenceActivity.class);
				MainActivity.this.startActivity(intent);

				break;
			}
			case 1:{
				final EditText keytext = new EditText(MainActivity.this);
				keytext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setTitle("请输入密码");
				builder.setIcon(R.drawable.lock_light);
				builder.setView(keytext);
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (PreferenceInfo.userPasswordValue.equals(keytext.getText().toString())) {
							PreferenceInfo.appLock(true);
							Toast.makeText(MainActivity.this, "已锁定", Toast.LENGTH_LONG).show();
							MainActivity.this.onResume();
						}
						else {
							Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_LONG).show();
						}
					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
			}
			}

			return false;
		}

    }

    /**
	 * 短按日志事件
	 */
	private class OnItemSelectedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			note = mNoteList.get(position);
            Log.d(TAG,"position: "+position+ " id " +id);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, EditActivity.class);
			Bundle bundle = new Bundle();
	    	bundle.putParcelable("note", note);
	    	intent.putExtra("noteBundle", bundle);

	    	MainActivity.this.startActivity(intent);
		}
	}

	/**
	 * 上下文菜单创建
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderIcon(R.drawable.option_light);
		menu.setHeaderTitle("日志选项");
		menu.add(0, 1, 1, "删除");
		menu.add(0, 2, 2, "短信发送");
	}

	/**
	 * 上下文菜单事件
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int index = info.position;
		final Note note = mNoteList.get(index);

		switch (item.getItemId()) {
		case 1: {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("删除");
			builder.setIcon(R.drawable.delete_light);
			builder.setMessage("您确定要把日志删除吗？");
			builder.setPositiveButton("确定",new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					NoteDBAccess access = new NoteDBAccess(MainActivity.this);
					access.deleteNote(note);
						
					dialog.dismiss();
					Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_LONG).show();
					flush();
				}
			});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			
			break;
		}
		case 2: {
			Intent iIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
			
			if (!note.getNoteContent().equals(note.getNoteTitle())) {
				iIntent.putExtra("sms_body", note.getNoteTitle() + "\n" + note.getNoteContent());
			}
			else {
				iIntent.putExtra("sms_body", note.getNoteContent());
			}
			MainActivity.this.startActivity(iIntent);
			
			break;
		}
		}
		
		return super.onContextItemSelected(item);
	}

	/**
	 * 界面刷新
	 */

    private void flush() {
    	PreferenceInfo.dataFlush();
    	
    	mNoteList = access.findAllNote();
    	
    	noteBaseAdapter = new NoteBaseAdapter(this, R.layout.note_list_item, mNoteList);
    	noteListView.setAdapter(noteBaseAdapter);
    	noteNumTextView.setText(mNoteList.size() + "");
    }
    
}