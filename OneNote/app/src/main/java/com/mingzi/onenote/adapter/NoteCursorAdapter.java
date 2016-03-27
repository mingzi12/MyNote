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
import com.mingzi.onenote.util.DBAccess;
import com.mingzi.onenote.util.DBOpenHelpe;
import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.util.ArrayList;
import java.util.List;

public class NoteCursorAdapter extends CursorAdapter {
	
	private Context context;
	private Cursor c;
	private LayoutInflater layoutInflater;
	private View view;
	private DBOpenHelpe mDbutil;
	
	private List<Note> list = new ArrayList<Note>();
	
	/**
	 * @return the list
	 */
	public List<Note> getList() {
		return this.list;
	}

	public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		this.context = context;
		this.c = c;
		
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView imNoteIcon = (TextView)view.findViewById(R.id.itemimage);
		TextView tvNoteTitle = (TextView)view.findViewById(R.id.itemtitle);
		TextView tvNoteDate = (TextView)view.findViewById(R.id.itemdate);

		imNoteIcon.setText((cursor.getPosition() + 1) + "");
		imNoteIcon.setBackgroundColor(PreferenceInfo.themeColorValue);
		tvNoteTitle.setText(cursor.getString(cursor.getColumnIndex("notetitle")));
		tvNoteDate.setText(cursor.getString(cursor.getColumnIndex("notedate")));
		
		Note note = new Note();
		note.setNoteId(c.getInt(c.getColumnIndex(ConstantValue.DB_MetaData.NOTEID_COL)));
		note.setNoteTitle(c.getString(c.getColumnIndex(ConstantValue.DB_MetaData.NOTETITLE_COL)));
		note.setNoteContent(c.getString(c.getColumnIndex(ConstantValue.DB_MetaData.NOTECONTENT_COL)));
		note.setNoteDate(ConvertStringAndDate.stringtodate(c.getString(c.getColumnIndex(ConstantValue.DB_MetaData.NOTEDATE_COL))));
		
		list.add(note);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		view = layoutInflater.inflate(R.layout.item, null);
		return view;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex("notetitle"));
		return name;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		c.moveToFirst();
		
		if(null == mDbutil){
			mDbutil = new DBOpenHelpe(context);
		}
		
		list.clear();
		if(null != constraint){
			String[] selectionArgs = new String[]{"%"+constraint.toString()+"%", "%"+constraint.toString()+"%"};
			String selection = "notetitle like ? or notecontent like ?";
			
			c = new DBAccess(context).selectAllNoteCursor(selection, selectionArgs);
		}
		else
		{
			c = new DBAccess(context).selectAllNoteCursor(null, null);
		}
		
		return c;
	}
}
