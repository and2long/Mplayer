package com.and2long.mplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.and2long.mplayer.R;
import com.and2long.mplayer.bean.VideoInfo;
import com.and2long.mplayer.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by L on 2016/12/3.
 */

public class VideoListAdapter extends CursorAdapter {


    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context, R.layout.item_video_info, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = getHolder(view);
        VideoInfo videoInfo = VideoInfo.fromCursor(cursor);
        holder.videoName.setText(videoInfo.getTittle());
        holder.videoLength.setText(StringUtil.formatMediaDuration(videoInfo.getDuration()));
    }

    private ViewHolder getHolder(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }

    static class ViewHolder {
        @BindView(R.id.tv_video_length)
        TextView videoLength;
        @BindView(R.id.tv_video_name)
        TextView videoName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
