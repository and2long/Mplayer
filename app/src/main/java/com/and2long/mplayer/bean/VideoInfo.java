package com.and2long.mplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by and2long on 2016/12/7.
 */

public class VideoInfo implements Serializable {

    private String tittle, path;
    private long size, duration;


    /**
     * 将cursor中的数据封装成一个bean
     * @param cursor
     * @return
     */
    public static VideoInfo fromCursor(Cursor cursor) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        videoInfo.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        videoInfo.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        videoInfo.setTittle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        return videoInfo;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
