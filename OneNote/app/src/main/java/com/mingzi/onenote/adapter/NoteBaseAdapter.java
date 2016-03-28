/**
 * @author LHT
 */
package com.mingzi.onenote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.ConvertStringAndDate;
import com.mingzi.onenote.vo.Note;
import com.mingzi.onenote.vo.PreferenceInfo;

import java.util.Date;
import java.util.List;

public class NoteBaseAdapter extends BaseAdapter {

	private List<Note> list;
	private Context context;
	private int resource;
	
	public NoteBaseAdapter(Context context, int resource, List<Note> list) {
		this.context = context;
		this.resource = resource;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (list.get(position)).getNoteId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Note note = list.get(position);
		String noteTitle = note.getNoteTitle();
		Date noteDate = note.getNoteDate();
				
		LayoutInflater layoutInflater = LayoutInflater.from(context);
				
		View view = layoutInflater.inflate(resource, null);
		view.setBackgroundColor(PreferenceInfo.themeColorValue);
		TextView tvNoteTitle = (TextView)view.findViewById(R.id.itemtitle);
		TextView tvNoteDate = (TextView)view.findViewById(R.id.itemdate);
		tvNoteTitle.setText(noteTitle);
		tvNoteDate.setText(ConvertStringAndDate.datetoString(noteDate));

		return view;
	}

}
