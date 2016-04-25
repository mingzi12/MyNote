/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.adapter.NoteBaseAdapter;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MainActivity extends Activity {

    public static final String TAG = "EditActivity ----> ";

    private ListView noteListView;
    private NoteBaseAdapter noteBaseAdapter;
    private NoteDBAccess access;
    private List<Note> mNoteList;
    private Note note = new Note();
    private PreferenceInfo mPreferenceInfo;
    private boolean isExit = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("所有便签");
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        noteListView = (ListView) findViewById(R.id.notelist);
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
                    } else {
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
     * 初始化ActionBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 响应ActionBar中每个子项的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.ab_add_main:
                intent = new Intent();
                intent.setClass(MainActivity.this, NewNoteActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.ab_search_main:
                intent = new Intent();
                intent.setClass(MainActivity.this, SearchActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.action_settings:
                intent = new Intent();
                intent.setClass(MainActivity.this, OneNotePreferenceActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 短按日志事件
     */
    private class OnItemSelectedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            note = mNoteList.get(position);
            Log.d(TAG, "position: " + position + " id " + id);
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
     *
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderIcon(R.drawable.item_setting_dark);
        menu.setHeaderTitle("日志选项");
        menu.add(0, 1, 1, "删除");
        menu.add(0, 2, 2, "短信发送");
    }

    /**
     * 上下文菜单事件
     *
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        final Note note = mNoteList.get(index);

        switch (item.getItemId()) {
            case 1: {
                AlertDialog.Builder builder = new Builder(MainActivity.this);
                builder.setTitle("删除");
                builder.setIcon(R.drawable.delete_light);
                builder.setMessage("您确定要把日志删除吗？");
                builder.setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NoteDBAccess access = new NoteDBAccess(MainActivity.this);
                        access.deleteNoteById(note);

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
                } else {
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

        mNoteList = access.selectAllNote();

        noteBaseAdapter = new NoteBaseAdapter(this, R.layout.note_list_item, mNoteList);
        noteListView.setAdapter(noteBaseAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}