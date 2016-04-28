/**
 * @author LHT
 */
package com.mingzi.onenote.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.adapter.NoteBaseAdapter;
import com.mingzi.onenote.util.MediaDBAccess;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MainActivity extends Activity implements AdapterView.OnItemLongClickListener{

    public static final String TAG = "MainActivity ";

    private ListView noteListView;
    private GridView mGridView;
    private NoteBaseAdapter noteBaseAdapter;
    private NoteDBAccess access;
    private List<Note> mNoteList;
    private Note note = new Note();
    private PreferenceInfo mPreferenceInfo;
    private boolean isExit = false;
    private int mViewForm;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        noteListView = (ListView) findViewById(R.id.notelist);
        noteListView.setOnItemClickListener(new OnItemSelectedListener());
        noteListView.setOnItemLongClickListener(this);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setOnItemClickListener(new OnItemSelectedListener());
        mGridView.setOnItemLongClickListener(this);
        mNoteList = new ArrayList<Note>();
        access = new NoteDBAccess(this);
        mPreferenceInfo = PreferenceInfo.getPreferenceInfo(this);
        mViewForm = mPreferenceInfo.viewForm();
        this.registerForContextMenu(noteListView);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

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
            case R.id.view_by_list:
                mPreferenceInfo.setViewForm(0);
                mViewForm = 0;
                flush();
                break;
            case R.id.view_by_grid:
                mPreferenceInfo.setViewForm(1);
                mViewForm = 1;
                flush();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
        int id1 = mNoteList.get(position).getNoteId();
        Log.d(TAG, "onItemLongClick: "+ id + " "+ id1);
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setTitle("删除");
        builder.setIcon(R.drawable.ic_delete);
        builder.setMessage("您确定要把日志删除吗？");
        builder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NoteDBAccess access = new NoteDBAccess(MainActivity.this);
                MediaDBAccess mediaDBAccess = new MediaDBAccess(MainActivity.this);
                mediaDBAccess.deleteById((int)id);
                access.deleteNoteById((int)id);

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_LONG).show();
                flush();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
        return false;
    }


    /**
     * 界面刷新
     */

    private void flush() {
        mPreferenceInfo.dataFlush();

        mNoteList = access.selectAllNote();

        if (mViewForm==0) {
            noteBaseAdapter = new NoteBaseAdapter(this, R.layout.note_list_item, mNoteList);
            noteListView.setAdapter(noteBaseAdapter);
            noteListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        } else if (mViewForm == 1) {
            noteBaseAdapter = new NoteBaseAdapter(this, R.layout.note_grid_item, mNoteList);
            mGridView.setAdapter(noteBaseAdapter);
            mGridView.setVisibility(View.VISIBLE);
            noteListView.setVisibility(View.GONE);
        } else {
            noteBaseAdapter = new NoteBaseAdapter(this, R.layout.note_list_item, mNoteList);
            noteListView.setAdapter(noteBaseAdapter);
            noteListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        }

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