package com.mingzi.onenote.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.adapter.NoteCursorAdapter;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.vo.Note;

public class SearchActivity extends Activity {

    private AutoCompleteTextView searchTextView;
    private ListView searchListView;

    private NoteCursorAdapter noteCursorAdapter;
    private Cursor c;
    private NoteDBAccess access;

    private Note note = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_search);

        access = new NoteDBAccess(this);

        c = access.selectAllNoteCursor(null, null);
        noteCursorAdapter = new NoteCursorAdapter(this, c, true);
        searchTextView = (AutoCompleteTextView) findViewById(R.id.searchtext);
        searchTextView.setDropDownHeight(0);
        searchTextView.requestFocus();
        searchTextView.setAdapter(noteCursorAdapter);

        searchListView = (ListView) findViewById(R.id.searchList);
        searchListView.setAdapter(noteCursorAdapter);
        searchListView.setOnItemClickListener(new OnItemSelectedListener());
    }

    /**
     * 短按日志事件
     */
    private class OnItemSelectedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            note = noteCursorAdapter.getList().get(position);

            Intent intent = new Intent();
            intent.setClass(SearchActivity.this, EditActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable("note", note);
            intent.putExtra("noteBundle", bundle);

            SearchActivity.this.startActivity(intent);
         SearchActivity.this.finish();
        }
    }

}
