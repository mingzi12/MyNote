package com.mingzi.onenote.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.mingzi.onenote.util.ConvertStringAndDate;

import java.util.Date;

/**
 * Created by Administrator on 2016/4/6.
 */
public class Media implements Parcelable {
    private String path;
    private int ownerId;
    private Date mDate;
    public Media() {
    }

    public Media(String path){
        this.path = path;
    }
    public Media(String path ,int id) {
        this.path = path;
        this.ownerId = id;
    }

    public Media(String path, int ownerId, Date date) {
        this.path = path;
        this.ownerId = ownerId;
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    Parcelable.Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            Media media = new Media();
            media.setPath(source.readString());
            media.setOwnerId(source.readInt());
            media.setDate(ConvertStringAndDate.stringtodate(source.readString()));
            return media;
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeInt(this.ownerId);
        dest.writeString(ConvertStringAndDate.datetoString(this.mDate));
    }
}
