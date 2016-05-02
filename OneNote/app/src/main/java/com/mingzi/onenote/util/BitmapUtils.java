package com.mingzi.onenote.util;

/**
 * Created by Administrator on 2016/4/2.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapUtils {

    public static Bitmap readBitMap(String fileName,int n) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = n; // width，hight设为原来的十分一
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(fis, null, opt);
    }


    public static Bitmap getBitmapByPath(String path){
        Bitmap bitmap ;
        if (path != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(path,options);
            return bitmap;
        }

        return null;
    }

    /**
     * 根据文件Uri获取路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByFileUri(Context context, Uri uri) {
        String filePath = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        if (cursor !=null) {
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndex("_data"));
        }
            cursor.close();
            return Uri.decode(filePath);
        }
       return null;
    }


}
