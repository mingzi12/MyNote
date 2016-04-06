package com.mingzi.onenote.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class MeidaDBAccess {

    private Context mContext;
    private List<Media> mList;
    private DBOpenHelper mDBOpenHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private static final String path = Environment.getExternalStorageDirectory()
            + File.separator+ "微盘" + File.separator + "123.jpg";
    public MeidaDBAccess(Context context) {
        mContext = context.getApplicationContext();
        mDBOpenHelper = new DBOpenHelper(mContext);
        mList = new ArrayList<>();

    }

    public void insert(){
        mSQLiteDatabase = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantValue.MediaMetaData.MEDIA_PATH, path);
        mSQLiteDatabase.insert(ConstantValue.MEDIA_TABLE_NAME, null, contentValues);
        closeDB();
    }

    public Cursor query(){
        mSQLiteDatabase = mDBOpenHelper.getReadableDatabase();
        Cursor cursor = mSQLiteDatabase.query(ConstantValue.MEDIA_TABLE_NAME,null,
                null,null,null,null,null);
        return cursor;
    }

    public List selectAll(){
        Cursor cursor = query();
        Media media = new Media();
        while (cursor.moveToNext()){
            media.setPath(cursor.getString(cursor.getColumnIndex(ConstantValue.MediaMetaData.MEDIA_PATH)));
            mList.add(media);
        }
        close();
        return mList;
    }

    public void closeDB(){
        if (mSQLiteDatabase != null){
            mSQLiteDatabase.close();
        }
    }

    public void close(){
        if (mSQLiteDatabase != null){
            mSQLiteDatabase.close();
        }
        if (mDBOpenHelper != null){
            mDBOpenHelper.close();
        }
    }
}
