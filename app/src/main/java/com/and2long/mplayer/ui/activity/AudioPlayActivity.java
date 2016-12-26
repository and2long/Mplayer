package com.and2long.mplayer.ui.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.and2long.mplayer.R;
import com.and2long.mplayer.bean.AudioInfo;
import com.and2long.mplayer.service.AudioPlayService;
import com.and2long.mplayer.util.StringUtil;
import com.and2long.mplayer.view.MarqeeTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by L on 2016/12/20.
 */

public class AudioPlayActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "AudioPlayActivity";
    private static final int MSG_UPDATE_PLAY_PROGRESS = 0;
    @BindView(R.id.tv_title)
    MarqeeTextView tvTitle;
    @BindView(R.id.tv_singer)
    MarqeeTextView tvSinger;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_previous)
    ImageView ivPrevious;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    private GestureDetector mGestureDetector;
    private ArrayList<AudioInfo> audioInfos;//音乐列表
    private AudioPlayService.MyBinder binder;//音乐播放代理
    private MyServiceConnection serviceConnection;
    private Handler handler;
    private AudioPlayBrodcastReceiver audioPlayBrodcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);
        ButterKnife.bind(this);

        init();
        //注册广播
        registerBroadcast();
    }

    private void registerBroadcast() {
        audioPlayBrodcastReceiver = new AudioPlayBrodcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.and2long.audio_play_service");
        registerReceiver(audioPlayBrodcastReceiver, filter);
    }

    /**
     * 初始化数据
     */
    private void init() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_PLAY_PROGRESS:
                        updatePlayProgress();
                        break;
                }
            }
        };
        //创建手势识别监听器
        initGestureDetector();
        //获取当前播放位置和音乐列表
        int playPosition = getIntent().getIntExtra("playPosition", 0);
        audioInfos = (ArrayList<AudioInfo>) getIntent().getSerializableExtra("audioInfos");
        boolean playState = getIntent().getBooleanExtra("playState", false);
        //绑定服务
        Intent audioPlayServiceIntent = new Intent(this, AudioPlayService.class);
        serviceConnection = new MyServiceConnection();
        bindService(audioPlayServiceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
        if (audioInfos == null || audioInfos.isEmpty()) {
            Log.i(TAG, "updateLayoutInfos: 数据为空");
            return;
        }
        seekBar.setOnSeekBarChangeListener(this);
        //更新界面状态
        updateLayoutInfos(playPosition, playState);
    }

    /**
     * 更新播放进度
     */
    private void updatePlayProgress() {
        final long progress = binder.getProgress();
        tvProgress.setText(StringUtil.formatMediaDuration(progress));
        seekBar.setProgress((int) progress);
        handler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_PROGRESS, 500);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            binder.seekTo(progress);
            tvProgress.setText(StringUtil.formatMediaDuration(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AudioPlayService.MyBinder) service;
            updatePlayProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    /**
     * 广播接受者
     */
    class AudioPlayBrodcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int playPosition = intent.getIntExtra("playPosition", -1);
            boolean playState = intent.getBooleanExtra("playState", false);
            updateLayoutInfos(playPosition, playState);
        }
    }

    /**
     * 初始化手势识别器
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) < 200) {
                    Log.i(TAG, "onFling: 移动太慢，无效操作。");
                    return true;
                }
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Log.i(TAG, "onFling: 垂直方向移动过大，无效操作");
                    return true;
                }
                if (e2.getRawX() - e1.getRawX() > 200) {
                    finish();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //让手势识别器识别手势事件。
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @OnClick({R.id.iv_play, R.id.iv_previous, R.id.iv_next, R.id.rl_back})
    public void onClick(View view) {
        if (binder == null) {
            Log.i(TAG, "onClick: binder为空");
            return;
        }
        switch (view.getId()) {
            case R.id.iv_play:
                if (binder.isPlaying()) {
                    binder.pause();
                } else {
                    binder.start();
                }
                break;
            case R.id.iv_previous:
                binder.playPre();
                break;
            case R.id.iv_next:
                binder.playNext();
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    /**
     * 更新界面各属性
     */
    private void updateLayoutInfos(int playPosition, boolean playState) {
        if (audioInfos == null || audioInfos.isEmpty()) {
            return;
        }
        tvSinger.setText(audioInfos.get(playPosition).getSinger());
        tvTitle.setText(audioInfos.get(playPosition).getTittle());
        tvDuration.setText(StringUtil.formatMediaDuration(audioInfos.get(playPosition).getDuration()));
        seekBar.setMax((int) audioInfos.get(playPosition).getDuration());
        ivPlay.setBackgroundResource(
                playState ?
                        R.drawable.ic_pause_circle_outline_selector : R.drawable.ic_play_circle_outline_selector);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(audioPlayBrodcastReceiver);
    }
}
