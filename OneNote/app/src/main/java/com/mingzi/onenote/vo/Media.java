package com.mingzi.onenote.vo;

/**
 * Created by Administrator on 2016/4/6.
 */
public class Media {
    private String path;
    private int ownerId;
    public Media() {
    }

    public Media(String path ,int id) {
        this.path = path;
        this.ownerId = id;
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
}
