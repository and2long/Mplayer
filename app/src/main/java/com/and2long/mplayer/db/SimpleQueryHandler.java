package com.and2long.mplayer.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

import com.and2long.mplayer.adapter.AudioListAdapter;
import com.and2long.mplayer.adapter.VideoListAdapter;

/**
 * Created by L on 2016/12/3.
 */

public class SimpleQueryHandler extends AsyncQueryHandler {


    public SimpleQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        //更新adapter
        if (cookie != null) {
            if (cookie instanceof VideoListAdapter) {
                VideoListAdapter adapter = (VideoListAdapter) cookie;
                adapter.changeCursor(cursor);
            } else if (cookie instanceof AudioListAdapter) {
                AudioListAdapter adapter = (AudioListAdapter) cookie;
                adapter.changeCursor(cursor);
            }
        }
    }

}
