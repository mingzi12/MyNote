package com.mingzi.onenote.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mingzi.onenote.values.ConstantValue;
import com.mingzi.onenote.vo.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class MediaDBAccess {

    private static final String TAG = "MeidaAccess-";

    private Context mContext;
    private List<Media> mList;
    private DBOpenHelper mDBOpenHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public MediaDBAccess(Context context) {
        mContext = context.getApplicationContext();
        mDBOpenHelper = new DBOpenHelper(mContext);
        mList = new ArrayList<>();

    }

    public void insert(String path, int noteId, String dateStr){
        mSQLiteDatabase = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantValue.MEDIA_PATH, path);
        contentValues.put(ConstantValue.MEDIA_OWNER_ID,noteId);
        contentValues.put(ConstantValue.MEDIA_DATE,dateStr);
        mSQLiteDatabase.insert(ConstantValue.MEDIA_TABLE_NAME, null, contentValues);
        closeDB();
    }

    public Cursor queryAll(){
        mSQLiteDatabase = mDBOpenHelper.getReadableDatabase();
        Cursor cursor = mSQLiteDatabase.query(ConstantValue.MEDIA_TABLE_NAME,null,
                null,null,null,null,null);
        return cursor;
    }

    public Cursor queryById(int noteId){
        Log.d(TAG+"queryById",noteId+"");
        mSQLiteDatabase = mDBOpenHelper.getReadableDatabase();
        Cursor cursor = mSQLiteDatabase.query(ConstantValue.MEDIA_TABLE_NAME,null,ConstantValue.MEDIA_OWNER_ID
                +"= ?",new String[]{noteId+""},null,null,null);

        return cursor;
    }

    public List selectAll(int noteId){
        Cursor cursor = queryById(noteId);
        while (cursor.moveToNext()){
            Log.d(TAG + "selectAll", cursor.getString(1));
            Media media = new Media();
            media.setPath(cursor.getString(cursor.getColumnIndex(ConstantValue.MEDIA_PATH)));
            media.setDate(ConvertStringAndDate.stringtodate(cursor.getString(cursor.getColumnIndex(ConstantValue.MEDIA_DATE))));
            mList.add(media);
        }
        close();
        handleList();
        return mList;
    }

    public boolean deleteByPath(String path){
        mSQLiteDatabase = mDBOpenHelper.getWritableDatabase();
        mSQLiteDatabase.delete(ConstantValue.MEDIA_TABLE_NAME,
                ConstantValue.MEDIA_PATH + " = ?", new String[]{path});
        return true;
    }

    public boolean deleteById(int noteId){
        mSQLiteDatabase = mDBOpenHelper.getWritableDatabase();
        mSQLiteDatabase.delete(ConstantValue.MEDIA_TABLE_NAME,ConstantValue.MEDIA_OWNER_ID+"=?",
                new String[]{noteId +""});
        close();
        return true;
    }

    public void handleList(){
        Iterator<Media> iterator = mList.iterator();
        while (iterator.hasNext()){
            Media media =  iterator.next();
            Log.d(TAG+"handleList",media.getPath());
            if (!(new File(media.getPath()).exists())){
                iterator.remove();
                deleteByPath(media.getPath());
            }
        }
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
