/**
 * @author LHT
 */
package com.mingzi.onenote.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.mingzi.onenote.values.ConstantValue;
public class DBOpenHelper extends SQLiteOpenHelper {
	

	public DBOpenHelper(Context context) {
		this(context, ConstantValue.DB_NAME, null, ConstantValue.VERSION);
	}
	
	public DBOpenHelper(Context context, int version) {
		this(context, ConstantValue.DB_NAME, null, version);
	}
	
	public DBOpenHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}
	
	public DBOpenHelper(Context context, String name, CursorFactory factory,
                        int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * 创建数据库函数，回调函数，在程序运行中只会调用一次
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + ConstantValue.NOTE_TABLE_NAME + "("
                + ConstantValue.NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ConstantValue.NOTE_TITLE + " TEXT NOT NULL DEFAULT \"\", "
                + ConstantValue.NOTE_CONTENT + " TEXT NOT NULL DEFAULT \"\" , "
                + ConstantValue.NOTE_DATE + " DATE)");
        db.execSQL("CREATE TABLE "+ConstantValue.MEDIA_TABLE_NAME+"("
                + ConstantValue.NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ConstantValue.MEDIA_PATH + " TEXT NOT NULL DEFAULT \"\","
                + ConstantValue.MEDIA_OWNER_ID + " INTEGER NOT NULL DEFAULT 0,"
                +ConstantValue.MEDIA_DATE + " DATE)");
	}
	
	/**
	 * 更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ConstantValue.NOTE_TABLE_NAME);
		onCreate(db);
	}
		
	/**
	 * 关闭数据库
	 */
	public static void closeDB(SQLiteDatabase db) {
		if(db != null) {
			db.close();
		}
	}
	
	/**
	 *释放游标 
	 */
	public static void closeCursor(Cursor c) {
		if (c != null) {
			c.close();
		}
	}
}
