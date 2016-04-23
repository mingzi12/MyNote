package com.mingzi.onenote.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDBAccess {
    private static final String TAG = "NoteDBAccess";
	private DBOpenHelper mDbOpenHelper;
	private SQLiteDatabase db;
	
	/**
	 * 列名
	 */
	private static String[] colNames = new String[]{
		ConstantValue.NOTE_ID,
		ConstantValue.NOTE_TITLE,
		ConstantValue.NOTE_CONTENT,
		ConstantValue.CREATE_DATE,
        ConstantValue.UPDATE_DATE
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
		cv.put(ConstantValue.CREATE_DATE, ConvertStringAndDate.datetoString(note.getCreateDate()));
        cv.put(ConstantValue.UPDATE_DATE,ConvertStringAndDate.datetoString(note.getUpdateDate()));
		db.insert(ConstantValue.NOTE_TABLE_NAME, null, cv);
        db.close();
	}
	
	public int insertNullNote(Note note){
        db = mDbOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantValue.CREATE_DATE,
                ConvertStringAndDate.datetoString(note.getCreateDate()));
        contentValues.put(ConstantValue.UPDATE_DATE,
                ConvertStringAndDate.datetoString(note.getCreateDate()));

       return (int)db.insert(ConstantValue.NOTE_TABLE_NAME,null,contentValues);
    }
	/**
	 * 删除Note
	 * @param note
	 */
	public void deleteNoteById(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		db.delete(ConstantValue.NOTE_TABLE_NAME,
                ConstantValue.NOTE_ID + "= ?", new String[]{note.getNoteId() + ""});
        db.close();
	}
	
	/**
	 * 更新Note
	 * @param note
	 */
	public void updateNoteById(Note note) {
		db = mDbOpenHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(ConstantValue.NOTE_TITLE, note.getNoteTitle());
		cv.put(ConstantValue.NOTE_CONTENT, note.getNoteContent());
		cv.put(ConstantValue.UPDATE_DATE, ConvertStringAndDate.datetoString(note.getCreateDate()));
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
			String createDate = c.getString(c.getColumnIndex(ConstantValue.CREATE_DATE));
            String updateDate = c.getString(c.getColumnIndex(ConstantValue.UPDATE_DATE));

			
			mNoteList.add(new Note(noteID, noteTitle, noteContent,
                    ConvertStringAndDate.stringtodate(createDate),
                    ConvertStringAndDate.stringtodate(updateDate)));
		}
		DBOpenHelper.closeCursor(c);
		DBOpenHelper.closeDB(db);
		return mNoteList;
	}

    public Note selectNoteById(int id) {
        db = mDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(ConstantValue.NOTE_TABLE_NAME,colNames,ConstantValue.NOTE_ID+"= ?",
                new String[]{id+" "},null,null,null );
        String noteTitle =null;
        String noteContent = null;
        if (cursor.moveToFirst()) {
             noteTitle = cursor.getString(cursor.getColumnIndex(ConstantValue.NOTE_TITLE));
             noteContent = cursor.getString(cursor.getColumnIndex(ConstantValue.NOTE_CONTENT));
        }
        Log.d(TAG, "selectNoteById: "+noteContent);
        close();
        cursor.close();
        return new Note(noteTitle,noteContent);
    }

    private void close() {
        if (mDbOpenHelper != null) {
            mDbOpenHelper.close();
        }
        if (db != null) {
            db.close();
        }
    }
	
}
