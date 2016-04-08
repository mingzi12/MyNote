package com.mingzi.onenote.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.MeidaDBAccess;
import com.mingzi.onenote.util.MyBitmap;
import com.mingzi.onenote.vo.Media;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class MediaBaseAdapter extends BaseAdapter {
    private Context mContext;
    private List<Media> mMediaList;
    private Media mMedia;
    private Bitmap mBitmap;
    private String path;
    private File mFile;
    private MeidaDBAccess mMeidaDBAccess;
    public MediaBaseAdapter(Context context, List<Media> mediaList) {
        mContext = context.getApplicationContext();
        mMediaList = mediaList;
        mMeidaDBAccess = new MeidaDBAccess(mContext);
    }

    @Override
    public int getCount() {
        return mMediaList.size();
    }

    @Override
    public Media getItem(int position) {
        return mMediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.media_list_item, null);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.image_item);
            viewHolder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.mImageView.setPadding(5, 5, 5, 5);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mMedia = mMediaList.get(position);
        path = mMedia.getPath();
        if (path.endsWith(".jpg")){
            mBitmap = MyBitmap.readBitMap(path,8);
        }
        else if (path.endsWith(".mp4")){
            mBitmap = MyBitmap.getVideoThumbnail(path,600,800, MediaStore.Images.Thumbnails.MICRO_KIND);
        }
            viewHolder.mImageView.setImageBitmap(mBitmap);
        return convertView;
    }

    static class ViewHolder{
        ImageView mImageView;
    }

    public void addItem(Media media){
        mMediaList.add(media);
    }

    public void recycleBitmap(){
        if(mBitmap != null){
            mBitmap.recycle();
        }
    }
}
