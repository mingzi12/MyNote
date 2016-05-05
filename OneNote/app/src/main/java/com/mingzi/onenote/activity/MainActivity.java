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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.adapter.NoteBaseAdapter;
import com.mingzi.onenote.util.MediaDBAccess;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.util.SortByCreateDateAsc;
import com.mingzi.onenote.util.SortByCreateDateDesc;
import com.mingzi.onenote.util.SortByUpadteDateDesc;
import com.mingzi.onenote.util.SortByUpdateDateAsc;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 */
public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

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
    private int mSortForm;
    LinearLayout layout1;
    LinearLayout layout2;
    LinearLayout layout3;
    LinearLayout layout4;
    AlertDialog mDialog;
    Builder mBuilder;

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
        mNoteList = new ArrayList<>();
        access = new NoteDBAccess(this);
        mPreferenceInfo = PreferenceInfo.getPreferenceInfo(this);
        mPreferenceInfo.dataFlush();
        mViewForm = mPreferenceInfo.getViewForm();
        mSortForm = mPreferenceInfo.getSortForm();
        this.registerForContextMenu(noteListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteList = access.selectAllNote();
        sortList();
        flush();

    }

    private void sortList() {
        if (mSortForm == 0) {
            Collections.sort(mNoteList, new SortByUpdateDateAsc());
        } else if (mSortForm == 1) {
            Collections.sort(mNoteList, new SortByUpadteDateDesc());
        } else if (mSortForm == 2) {
            Collections.sort(mNoteList, new SortByCreateDateAsc());
        } else if (mSortForm == 3) {
            Collections.sort(mNoteList, new SortByCreateDateDesc());
        }
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
                    e.printStackTrace();
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
        MenuItem subMenu = menu.findItem(R.id.view_by_list);
        if (mViewForm == 0) {
            subMenu.setTitle("   缩略图").setIcon(R.drawable.ic_menu_grid_light);
        }
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
                if (mViewForm == 0) {
                    mPreferenceInfo.setViewForm(1);
                    mViewForm = 1;
                    item.setTitle("    列表");
                    item.setIcon(R.drawable.ic_menu_list_light);
                } else if (mViewForm == 1) {
                    mPreferenceInfo.setViewForm(0);
                    mViewForm = 0;
                    item.setTitle("    缩略图");
                    item.setIcon(R.drawable.ic_menu_grid_light);
                }
                Log.d(TAG, "onOptionsItemSelected: " + mViewForm);
                flush();
                break;
            case R.id.sort:
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.sort_choice_layout, null);
                layout1 = (LinearLayout) linearLayout.findViewById(R.id.sort_by_update_date_asc);
                layout2 = (LinearLayout) linearLayout.findViewById(R.id.sort_by_update_date_desc);
                layout3 = (LinearLayout) linearLayout.findViewById(R.id.sort_by_create_date_asc);
                layout4 = (LinearLayout) linearLayout.findViewById(R.id.sort_by_create_date_desc);
                layout1.setOnClickListener(this);
                layout2.setOnClickListener(this);
                layout3.setOnClickListener(this);
                layout4.setOnClickListener(this);
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("排序")
                        .setView(linearLayout);
                mDialog = mBuilder.create();
                mDialog.show();


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
     * 点击便签事件
     */

    private class OnItemSelectedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            note = mNoteList.get(position);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, EditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(EditActivity.NOTE, note);
            intent.putExtra(EditActivity.NOTE_BUNDLE, bundle);

            MainActivity.this.startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
        int id1 = mNoteList.get(position).getNoteId();
        Log.d(TAG, "onItemLongClick: " + id + " " + id1);
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setTitle("    删除");
        builder.setIcon(R.drawable.ic_delete);
        builder.setMessage("您确定要把日志删除吗？");
        builder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NoteDBAccess access = new NoteDBAccess(MainActivity.this);
                MediaDBAccess mediaDBAccess = new MediaDBAccess(MainActivity.this);
                mediaDBAccess.deleteById((int) id);
                access.deleteNoteById((int) id);
                mNoteList.remove(position);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_LONG).show();
                flush();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_by_update_date_asc:
                Collections.sort(mNoteList, new SortByUpdateDateAsc());
                mDialog.dismiss();
                flush();
                mSortForm = 0;
                mPreferenceInfo.setSortForm(0);
                break;
            case R.id.sort_by_update_date_desc:
                Collections.sort(mNoteList, new SortByUpadteDateDesc());
                mDialog.dismiss();
                flush();
                mSortForm = 1;
                mPreferenceInfo.setSortForm(1);
                break;
            case R.id.sort_by_create_date_asc:
                Collections.sort(mNoteList, new SortByCreateDateAsc());
                mDialog.dismiss();
                flush();
                mSortForm = 2;
                mPreferenceInfo.setSortForm(2);
                break;
            case R.id.sort_by_create_date_desc:
                Collections.sort(mNoteList, new SortByCreateDateDesc());
                mDialog.dismiss();
                flush();
                mSortForm = 3;
                mPreferenceInfo.setSortForm(3);
                break;
            default:
                break;
        }
    }

    /**
     * 界面刷新
     */

    private void flush() {

        if (mViewForm == 1) {
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