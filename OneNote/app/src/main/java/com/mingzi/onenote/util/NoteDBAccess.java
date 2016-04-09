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
		ConstantValue.NOTE_ID,
		ConstantValue.NOTE_TITLE,
		ConstantValue.NOTE_CONTENT,
		ConstantValue.NOTE_DATE
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
		cv.put(ConstantValue.NOTE_TITLE, note.getNoteTitle());
		cv.put(ConstantValue.NOTE_CONTENT, note.getNoteContent());
		cv.put(ConstantValue.NOTE_DATE, ConvertStringAndDate.datetoString(note.getNoteDate()));
		db.insert(ConstantValue.NOTE_TABLE_NAME, null, cv);
        db.close();
	}
	
	public int insertNullNote(Note note){
        db = mDbOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantValue.NOTE_DATE,
                ConvertStringAndDate.datetoString(note.getNoteDate()));
       return (int)db.insert(ConstantValue.NOTE_TABLE_NAME,null,contentValues);
    }
	/**
	 * 删除Note
	 * @param note
	 */
	public void deleteNoteById(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		db.delete(ConstantValue.NOTE_TABLE_NAME,
				ConstantValue.NOTE_ID + "= ?",new String[]{note.getNoteId()+""} );
        db.close();
	}
	
	/**
	 * 更新Note
	 * @param note
	 */
	public void updateNote(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(ConstantValue.NOTE_TITLE, note.getNoteTitle());
		cv.put(ConstantValue.NOTE_CONTENT, note.getNoteContent());
		cv.put(ConstantValue.NOTE_DATE, ConvertStringAndDate.datetoString(note.getNoteDate()));
		db.update(ConstantValue.NOTE_TABLE_NAME, cv,
                ConstantValue.NOTE_ID + "= ?", new String[]{note.getNoteId() + ""});
        db.close();
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
				selection, selectionArgs, null, null, ConstantValue.DEFAULT_ORDER);
		return c;
	}
	
	/**
	 * 获取Note列表
	 * @return
	 */
	public List<Note> selectAllNote() {
		Cursor c = selectAllNoteCursor(null, null);
		List<Note> mNoteList = new ArrayList<>();
		while (c.moveToNext()) {
			int noteID = c.getInt(c.getColumnIndex(ConstantValue.NOTE_ID));
			String noteTitle = c.getString(c.getColumnIndex(ConstantValue.NOTE_TITLE));
			String noteContent = c.getString(c.getColumnIndex(ConstantValue.NOTE_CONTENT));
			String noteDate = c.getString(c.getColumnIndex(ConstantValue.NOTE_DATE));
			
			mNoteList.add(new Note(noteID, noteTitle, noteContent, ConvertStringAndDate.stringtodate(noteDate)));
		}
		DBOpenHelper.closeCursor(c);
		DBOpenHelper.closeDB(db);
		return mNoteList;
	}
	
}
