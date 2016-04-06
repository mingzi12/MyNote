/**
 * @author LHT
 */
package com.mingzi.onenote.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.ConvertStringAndDate;
import com.mingzi.onenote.util.NoteDBAccess;
import com.mingzi.onenote.util.DBOpenHelper;
import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteCursorAdapter extends CursorAdapter {
	
	private Context context;
	private Cursor cursor;
	private LayoutInflater layoutInflater;
	private View view;
	private DBOpenHelper mDBOpenHelper;
	
	private List<Note> list = new ArrayList<Note>();
	
	/**
	 * @return the list
	 */
	public List<Note> getList() {
		return this.list;
	}

	public NoteCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
		super(context, cursor, autoRequery);
		this.context = context;
		this.cursor = cursor;
		
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvNoteTitle = (TextView)view.findViewById(R.id.itemtitle);
		TextView tvNoteDate = (TextView)view.findViewById(R.id.itemdate);
		tvNoteTitle.setText(cursor.getString(cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_TITLE)));
		tvNoteDate.setText(cursor.getString(cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_DATE)));
		
		Note note = new Note();
		note.setNoteId(this.cursor.getInt(this.cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_ID)));
		note.setNoteTitle(this.cursor.getString(this.cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_TITLE)));
		note.setNoteContent(this.cursor.getString(this.cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_CONTENT)));
		note.setNoteDate(ConvertStringAndDate.stringtodate(this.cursor.getString(this.cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_DATE))));
		
		list.add(note);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		view = layoutInflater.inflate(R.layout.note_list_item, null);
		return view;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex(ConstantValue.NoteMetaData.NOTE_TITLE));
		return name;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		cursor.moveToFirst();
		
		if(null == mDBOpenHelper){
			mDBOpenHelper = new DBOpenHelper(context);
		}
		
		list.clear();
		if(null != constraint){
			String[] selectionArgs = new String[]{"%"+constraint.toString()+"%", "%"+constraint.toString()+"%"};
			String selection = ConstantValue.NoteMetaData.NOTE_TITLE+" like ? or "
                    +ConstantValue.NoteMetaData.NOTE_CONTENT+ " like ?";
			
			cursor = new NoteDBAccess(context).selectAllNoteCursor(selection, selectionArgs);
		}
		else
		{
			cursor = new NoteDBAccess(context).selectAllNoteCursor(null, null);
		}
		
		return cursor;
	}
}
