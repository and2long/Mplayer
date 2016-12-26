package com.and2long.mplayer.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.and2long.mplayer.R;
import com.and2long.mplayer.bean.VideoInfo;
import com.and2long.mplayer.util.StringUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by L on 2016/12/3.
 */
public class VideoPlayActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "VideoPlayActivity";
    private static final int MSG_UPDATE_PLAY_PROGRESS = 0;
    private static final int MSG_HIDE_CONTROL_LAYOUT = 1;

    @BindView(R.id.videoview)
    VideoView mVideoView;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tv_video_current_time)
    TextView tvVideoCurrentTime;
    @BindView(R.id.seekbar_video)
    SeekBar seekbar;
    @BindView(R.id.tv_video_total_time)
    TextView tvVideoTotalTime;
    @BindView(R.id.iv_video_previous)
    ImageView ivVideoPrevious;
    @BindView(R.id.iv_video_play)
    ImageView ivVideoPlay;
    @BindView(R.id.iv_video_next)
    ImageView ivVideoNext;
    @BindView(R.id.layout_bottom_control)
    LinearLayout layoutBottomControl;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PLAY_PROGRESS:
                    updatePlayProgress();
                    break;
                case MSG_HIDE_CONTROL_LAYOUT:
                    hideControlLayout();
                    break;
            }
        }
    };
    //视频集合
    private ArrayList<VideoInfo> videoinfos;
    //当前播放item
    private int currentPosition;
    //传递过来的bundle对象
    private Bundle bundle;
    //当前播放的视频
    private VideoInfo currentVideo;
    //手势识别器
    private GestureDetector gestureDetector;
    //控制面板显示状态
    private boolean isControlLayoutShow;
    //最大音量
    private int maxVolume;
    //当前音量
    private int currentVolume;
    //屏幕宽高
    private int screenWidthPixels, screenHeightPixels;
    private float downY;
    private AudioManager audioManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LibsChecker.checkVitamioLibs(this);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        //设置窗体全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //初始化数据
        initData();
        //设置标题栏
        mToolBar.setBackgroundColor(Color.parseColor("#55000000"));
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //创建手势识别
        gestureDetector = new GestureDetector(this, new MyOnGestureListner());
        //当布局创建完成时，底部控制面板隐藏
        layoutBottomControl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutBottomControl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mToolBar.animate().translationY(-mToolBar.getHeight());
                layoutBottomControl.animate().translationY(layoutBottomControl.getHeight());
            }
        });

        //音量相关数据
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//等级为0-15
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //屏幕相关
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidthPixels = displayMetrics.widthPixels;
        screenHeightPixels = displayMetrics.heightPixels;
        //判断是否为从文件发送播放请求
        Uri uri = getIntent().getData();
        if (uri != null) {
            //从文件发起的播放请求
            ivVideoPrevious.setEnabled(false);
            ivVideoNext.setEnabled(false);
            mVideoView.setVideoURI(uri);
            File file = new File(uri.getPath());
            //设置标题
            mToolBar.setTitle(file.getName());
            //设置seekbar拖动监听
            seekbar.setOnSeekBarChangeListener(this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //设置总时长
                    tvVideoTotalTime.setText(StringUtil.formatMediaDuration(mediaPlayer.getDuration()));
                    //设置进度条的最大值
                    seekbar.setMax((int) mediaPlayer.getDuration());
                    mediaPlayer.start();
                    ivVideoPlay.setImageResource(R.mipmap.ic_media_pause);
                    updatePlayProgress();
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    finish();
                }
            });
            return;
        }
        //获取传递过来的视频列表和当前点击位置并播放。
        bundle = getIntent().getExtras();
        currentPosition = bundle.getInt("currentPosition");
        videoinfos = (ArrayList<VideoInfo>) bundle.getSerializable("videoInfos");
        playVideo();
    }

    /**
     * 播放当前位置的视频
     */
    private void playVideo() {
        if (videoinfos == null || videoinfos.size() == 0) {
            return;
        }
        //当前视频对象
        currentVideo = videoinfos.get(currentPosition);
        //设置标题
        mToolBar.setTitle(currentVideo.getTittle());
        //设置总时长
        tvVideoTotalTime.setText(StringUtil.formatMediaDuration(currentVideo.getDuration()));
        //设置播放路径
        mVideoView.setVideoPath(currentVideo.getPath());
        //设置进度条的最大值
        seekbar.setMax((int) currentVideo.getDuration());
        //设置seekbar拖动监听
        seekbar.setOnSeekBarChangeListener(this);
        //设置准备监听
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
    }


    /**
     * mVideoView准备状态的回调
     *
     * @param mediaPlayer
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        ivVideoPlay.setImageResource(R.mipmap.ic_media_pause);
        updatePlayProgress();
    }

    /**
     * mVideoView播放完成的回调
     *
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        updateBtnPlayBG();
        if (currentPosition < videoinfos.size() - 1) {
            currentPosition++;
            playVideo();
        }
    }

    /**
     * 根据播放状态设置播放按钮的图标
     */
    private void updateBtnPlayBG() {
        ivVideoPlay.setImageResource(mVideoView.isPlaying() ? R.mipmap.ic_media_pause : R.mipmap.ic_media_play);
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.iv_video_previous, R.id.iv_video_play, R.id.iv_video_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_video_previous:
                handler.removeMessages(MSG_HIDE_CONTROL_LAYOUT);
                if (currentPosition > 0) {
                    currentPosition--;
                    playVideo();
                }
                handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_LAYOUT, 3000);
                break;
            case R.id.iv_video_play:
                handler.removeMessages(MSG_HIDE_CONTROL_LAYOUT);
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    updateBtnPlayBG();
                } else {
                    mVideoView.start();
                    updateBtnPlayBG();
                }
                handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_LAYOUT, 3000);
                break;
            case R.id.iv_video_next:
                handler.removeMessages(MSG_HIDE_CONTROL_LAYOUT);
                if (currentPosition < videoinfos.size() - 1) {
                    currentPosition++;
                    playVideo();
                }
                handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_LAYOUT, 3000);
                break;
        }
    }

    /**
     * 显示控制面板
     */
    private void showControlLayout() {
        mToolBar.animate().translationY(0).setDuration(0);
        layoutBottomControl.animate().translationY(0).setDuration(0);
        isControlLayoutShow = true;
        //延迟2秒隐藏控制面板
        handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_LAYOUT, 3000);
    }

    /**
     * 隐藏控制面板
     */
    private void hideControlLayout() {
        mToolBar.animate().translationY(-mToolBar.getHeight());
        layoutBottomControl.animate().translationY(layoutBottomControl.getHeight());
        isControlLayoutShow = false;
        handler.removeMessages(MSG_HIDE_CONTROL_LAYOUT);
    }

    /**
     * 更新播放进度
     */
    private void updatePlayProgress() {
        //设置当前播放时间
        tvVideoCurrentTime.setText(StringUtil.formatMediaDuration(mVideoView.getCurrentPosition()));
        //设置进度
        seekbar.setProgress((int) mVideoView.getCurrentPosition());
        //提示界面更新
        handler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_PROGRESS, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 屏幕触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                //手指滑动的距离
                float moveDistance = moveY - downY;
                if (Math.abs(moveDistance) < 20) {
                    break;
                }
                if (moveDistance > 0) {//向下为正
                    if (currentVolume > 0) {
                        currentVolume -= 1;
                    }
                } else {
                    if (currentVolume < maxVolume) {
                        currentVolume += 1;
                    }
                }
                //更新界面
                updateVolume();
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                tvVolume.setVisibility(View.GONE);
                downY = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 更新音量
     */
    private void updateVolume() {
        tvVolume.setVisibility(View.VISIBLE);
        tvVolume.setText(currentVolume + "");
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
    }

    /**
     * seekbar拖动监听
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mVideoView.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //拖动进度条时不予许隐藏控制面板
        handler.removeMessages(MSG_HIDE_CONTROL_LAYOUT);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //重新发消息隐藏控制面板
        handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_LAYOUT, 3000);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("视频格式不支持！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .create()
                        .show();
                break;
        }
        return true;
    }

    /**
     * 手势识别监听器
     */
    class MyOnGestureListner extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isControlLayoutShow) {
                hideControlLayout();
            } else {
                showControlLayout();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

}
