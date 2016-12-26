package com.and2long.mplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.and2long.mplayer.R;
import com.and2long.mplayer.bean.AudioInfo;
import com.and2long.mplayer.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by L on 2016/12/19.
 */

public class AudioListAdapter extends CursorAdapter {

    @Override
    public int getCount() {
        return super.getCount();
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context, R.layout.item_audio_info, null);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder holder = getHolder(view);
        AudioInfo audioInfo = AudioInfo.fromCursor(cursor);
        holder.audioName.setText(audioInfo.getTittle());
        holder.audioSinger.setText(StringUtil.formatMediaDuration(audioInfo.getDuration()));
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
        @BindView(R.id.tv_file_name)
        TextView audioName;
        @BindView(R.id.tv_singer)
        TextView audioSinger;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
