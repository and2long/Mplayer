package com.and2long.mplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by L on 2016/12/19.
 */

public class AudioInfo implements Serializable {

    private String tittle, path, singer;
    private long size, duration;

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

    /**
     * 将cursor中的数据封装成一个bean
     * @param cursor
     * @return
     */
    public static AudioInfo fromCursor(Cursor cursor) {
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        audioInfo.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        audioInfo.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        audioInfo.setTittle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        audioInfo.setSinger(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        return audioInfo;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }
}
