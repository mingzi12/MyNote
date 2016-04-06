package com.mingzi.onenote.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mingzi.onenote.R;
import com.mingzi.onenote.util.MyBitmap;
import com.mingzi.onenote.vo.Media;

import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class MediaBaseAdapter extends BaseAdapter {
    private Context mContext;
    private List<Media> mMediaList;
    private Media mMedia;
    private Bitmap mBitmap;
    public MediaBaseAdapter(Context context, List<Media> mediaList) {
        mContext = context.getApplicationContext();
        mMediaList = mediaList;
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
        mBitmap = MyBitmap.readBitMap(mMedia.getPath(), 6);
        viewHolder.mImageView.setImageBitmap(mBitmap);
        return convertView;
    }

    static class ViewHolder{
        ImageView mImageView;
    }

    public void recycleBitmap(){
        if(mBitmap != null){
            mBitmap.recycle();
        }
    }
}
