package com.and2long.mplayer.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.and2long.mplayer.R;
import com.and2long.mplayer.adapter.VideoListAdapter;
import com.and2long.mplayer.bean.VideoInfo;
import com.and2long.mplayer.db.SimpleQueryHandler;
import com.and2long.mplayer.ui.activity.VideoPlayActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by L on 2016/12/3.
 */

public class VideoFragment extends Fragment implements AdapterView.OnItemClickListener {


    @BindView(R.id.listView)
    ListView mListView;
    private VideoListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_listview, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //查询数据库，获得视频列表信息。
        SimpleQueryHandler queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());
        adapter = new VideoListAdapter(getActivity(), null, true);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA,};
        queryHandler.startQuery(0, adapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);
        //携带一个视频对象跳转到视频播放界面。
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("currentPosition", position);
        bundle.putSerializable("videoInfos", cursortoList(cursor));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 将cursor中的数据取出来放到集合中。
     *
     * @param cursor
     * @return
     */
    private ArrayList<VideoInfo> cursortoList(Cursor cursor) {
        ArrayList<VideoInfo> videoInfos = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            videoInfos.add(VideoInfo.fromCursor(cursor));
        }
        return videoInfos;
    }
}
