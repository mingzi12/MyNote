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
public class DBOpenHelpe extends SQLiteOpenHelper {
	

	public DBOpenHelpe(Context context) {
		this(context, ConstantValue.DB_NAME, null, ConstantValue.DB_VERSION);
	}
	
	public DBOpenHelpe(Context context, int version) {
		this(context, ConstantValue.DB_NAME, null, version);
	}
	
	public DBOpenHelpe(Context context, String name, int version) {
		this(context, name, null, version);
	}
	
	public DBOpenHelpe(Context context, String name, CursorFactory factory,
					   int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * 创建数据库函数，回调函数，在程序运行中只会调用一次
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + ConstantValue.TABLE_NAME + "(" +
				ConstantValue.DB_MetaData.NOTEID_COL + 
				" integer primary key autoincrement, " +
				ConstantValue.DB_MetaData.NOTETITLE_COL + 
				" varchar, " + ConstantValue.DB_MetaData.NOTECONTENT_COL + 
				" text, " + ConstantValue.DB_MetaData.NOTEDATE_COL + " date)");
	}
	
	/**
	 * 更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + ConstantValue.TABLE_NAME);
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
