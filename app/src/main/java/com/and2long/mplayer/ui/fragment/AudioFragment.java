package com.and2long.mplayer.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.and2long.mplayer.R;
import com.and2long.mplayer.adapter.AudioListAdapter;
import com.and2long.mplayer.bean.AudioInfo;
import com.and2long.mplayer.db.SimpleQueryHandler;
import com.and2long.mplayer.service.AudioPlayService;
import com.and2long.mplayer.ui.activity.AudioPlayActivity;
import com.and2long.mplayer.view.MarqeeTextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by L on 2016/12/3.
 */

public class AudioFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "AudioFragment";

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.tv_title)
    MarqeeTextView tvTitle;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    private AudioListAdapter adapter;
    private ArrayList<AudioInfo> audioInfos;
    private MyBroadcastReceiver receiver;
    private AudioPlayService.MyBinder binder;
    private MyServiceConnection serviceConnection;
    private Intent audioPlayServiceIntent;
    private boolean isFirstClick = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        receiver = new MyBroadcastReceiver();
        getActivity().registerReceiver(receiver, new IntentFilter("com.and2long.audio_play_service"));
        serviceConnection = new MyServiceConnection();
        audioPlayServiceIntent = new Intent(getActivity(), AudioPlayService.class);
        getActivity().bindService(audioPlayServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        //传递音乐列表给服务。
        audioPlayServiceIntent.putExtra("audioInfos", audioInfos);
        getActivity().startService(audioPlayServiceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        getActivity().unbindService(serviceConnection);
    }


    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AudioPlayService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int playPosition = intent.getIntExtra("playPosition", 0);
            boolean playState = intent.getBooleanExtra("playState", false);
            updateBottomControlLayout(playPosition, playState);
        }


    }

    private void updateBottomControlLayout(int playPosition, boolean playState) {
        if (audioInfos.isEmpty()) {
            return;
        }
        tvTitle.setText(audioInfos.get(playPosition).getTittle());
        ivPlay.setBackgroundResource(playState ?
                R.drawable.ic_pause_circle_outline_selector : R.drawable.ic_play_circle_outline_selector);
    }


    /**
     * 初始化数据，获取音频文件列表
     */
    private void initData() {
        //查询数据库，获得视频列表信息。
        SimpleQueryHandler queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());
        adapter = new AudioListAdapter(getActivity(), null, true);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Audio.Media.DATA};
        queryHandler.startQuery(0, adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

    }

    /**
     * 条目点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        audioPlayServiceIntent.putExtra("clickPosition", position);
        audioPlayServiceIntent.putExtra("audioInfos", audioInfos);
        getActivity().startService(audioPlayServiceIntent);
    }

    /**
     * 将cursor中的数据取出来放到集合中。
     *
     * @param cursor
     * @return
     */
    private ArrayList<AudioInfo> cursortoList(Cursor cursor) {
        audioInfos = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                audioInfos.add(AudioInfo.fromCursor(cursor));
            }
        }
        Log.i(TAG, "cursortoList: 数据个数：" + audioInfos.size());
        return audioInfos;
    }

    @Override
    public void onGlobalLayout() {
        cursortoList(adapter.getCursor());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        //弹出删除提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除")
                .setMessage(audioInfos.get(position).getTittle())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //删除文件
                        File file = new File(audioInfos.get(position).getPath());
                        boolean flag = file.delete();
                        if (flag) {
                            //强制更新媒体库
                            MediaScannerConnection.scanFile(getActivity(), new String[]{Environment
                                    .getExternalStorageDirectory().getAbsolutePath()}, null, null);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
        return true;
    }

    @OnClick({R.id.layout_bottom_control, R.id.iv_play, R.id.iv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_bottom_control:
                Intent intent = new Intent(getActivity(), AudioPlayActivity.class);
                intent.putExtra("playPosition", AudioPlayService.currentPosition);
                intent.putExtra("audioInfos", audioInfos);
                boolean playState = binder != null && binder.isPlaying();
                intent.putExtra("playState", playState);
                getActivity().startActivity(intent);
                break;
            case R.id.iv_play:
                if (isFirstClick) {
                    //模拟点击第一个条目。
                    audioPlayServiceIntent.putExtra("clickPosition", 0);
                    audioPlayServiceIntent.putExtra("audioInfos", audioInfos);
                    getActivity().startService(audioPlayServiceIntent);
                    isFirstClick = false;
                    return;
                }
                if (binder.isPlaying()) {
                    binder.pause();
                } else {
                    binder.start();
                }
                break;
            case R.id.iv_next:
                binder.playNext();
                break;
        }
    }


}
