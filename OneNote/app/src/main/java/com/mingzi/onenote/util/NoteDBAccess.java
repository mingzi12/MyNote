package com.mingzi.onenote.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDBAccess {
	private DBOpenHelper mDbOpenHelper;
	private SQLiteDatabase db;
	
	/**
	 * 列名
	 */
	private static String[] colNames = new String[]{
		ConstantValue.NoteMetaData.NOTE_ID,
		ConstantValue.NoteMetaData.NOTE_TITLE,
		ConstantValue.NoteMetaData.NOTE_CONTENT,
		ConstantValue.NoteMetaData.NOTE_DATE
	};
	
	public NoteDBAccess(Context context) {
		mDbOpenHelper = new DBOpenHelper(context);
	}
	
	/**
	 * 增加Note到数据库
	 * @param note
	 */
	public void insertNote(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(ConstantValue.NoteMetaData.NOTE_TITLE, note.getNoteTitle());
		cv.put(ConstantValue.NoteMetaData.NOTE_CONTENT, note.getNoteContent());
		cv.put(ConstantValue.NoteMetaData.NOTE_DATE,
				ConvertStringAndDate.datetoString(note.getNoteDate()));
		db.insert(ConstantValue.NOTE_TABLE_NAME, null, cv);
	}
	
	
	/**
	 * 删除Note
	 * @param note
	 */
	public void deleteNote(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		
		db.delete(ConstantValue.NOTE_TABLE_NAME,
				ConstantValue.NoteMetaData.NOTE_ID + "=" + note.getNoteId(), null);
	}
	
	/**
	 * 更新Note
	 * @param note
	 */
	public void updateNote(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(ConstantValue.NoteMetaData.NOTE_TITLE, note.getNoteTitle());
		cv.put(ConstantValue.NoteMetaData.NOTE_CONTENT, note.getNoteContent());
		cv.put(ConstantValue.NoteMetaData.NOTE_DATE,
				ConvertStringAndDate.datetoString(note.getNoteDate()));
		db.update(ConstantValue.NOTE_TABLE_NAME, cv,
				ConstantValue.NoteMetaData.NOTE_ID + "=" + note.getNoteId(), null);
	}
	
	/**
	 * 查找约束相关的所有Note
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public Cursor selectAllNoteCursor(String selection, String[] selectionArgs) {
		db = mDbOpenHelper.getReadableDatabase();
		Cursor c = db.query(ConstantValue.NOTE_TABLE_NAME, colNames,
				selection, selectionArgs, null, null, 
				ConstantValue.NoteMetaData.DEFAULT_ORDER);
		
		return c;
	}
	
	/**
	 * 获取Note列表
	 * @return
	 */
	public List<Note> findAllNote() {
		Cursor c = selectAllNoteCursor(null, null);
		List<Note> noteList = new ArrayList<Note>();
		
		while (c.moveToNext()) {
			int noteID = c.getInt(0);
			String noteTitle = c.getString(1);
			String noteContent = c.getString(2);
			String noteDate = c.getString(3);
			
			noteList.add(new Note(noteID, noteTitle, noteContent, ConvertStringAndDate.stringtodate(noteDate)));
		}
		
		DBOpenHelper.closeCursor(c);
		DBOpenHelper.closeDB(db);
		
		return noteList;
	}
	
}
