package com.and2long.mplayer.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.and2long.mplayer.R;
import com.and2long.mplayer.bean.AudioInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by L on 2016/12/20.
 * 音乐播放服务
 */

public class AudioPlayService extends Service {

    private static final String TAG = "AudioPlayService";
    public static int currentPosition;
    private ArrayList<AudioInfo> audioList;
    private MyBinder myBinder;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;


    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
        mediaPlayer = new MediaPlayer();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //将音乐列表界面的点击位置赋值给该服务的当前播放位置。
        currentPosition = intent.getIntExtra("clickPosition", 0);
        //音乐列表
        audioList = (ArrayList<AudioInfo>) intent.getSerializableExtra("audioInfos");
        //播放
        myBinder.playAudio();
        return START_STICKY;    //杀死服务后自动启动
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public class MyBinder extends Binder implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

        //播放
        public void playAudio() {
            if (audioList == null || audioList.isEmpty()) return;
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
            }
            try {
                mediaPlayer.setDataSource(audioList.get(currentPosition).getPath());
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            sendBroad();
//            sendNotification();
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            playNext();
        }

        /**
         * 是否在播放第一首
         *
         * @return
         */
        public boolean isPlayingFirst() {
            return currentPosition == 0;
        }

        /**
         * 是否在播放最后一首
         *
         * @return
         */
        public boolean isPlayingLast() {
            return currentPosition == (audioList.size() - 1);
        }

        public void playPre() {
            if (currentPosition > 0) {
                currentPosition--;
                playAudio();
            } else {
                Toast.makeText(AudioPlayService.this, "已经是第一首歌曲了！", Toast.LENGTH_SHORT).show();
            }
        }

        public void playNext() {
            if (audioList == null || audioList.isEmpty()) {
                return;
            }
            if (currentPosition < (audioList.size() - 1)) {
                currentPosition++;
                playAudio();
            } else {
                Toast.makeText(AudioPlayService.this, "已经是最后一首歌曲了！", Toast.LENGTH_SHORT).show();
            }
        }

        public void start() {
            if (mediaPlayer != null) {
                mediaPlayer.start();
//                sendNotification();
                sendBroad();
            } else {
                Log.i(TAG, "start: mediaPlayer未初始化");
            }
        }

        public void pause() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
//                stopForeground(true);
                sendBroad();
            } else {
                Log.i(TAG, "pause: mediaPlayer未初始化");
            }
        }

        private void sendBroad() {
            Intent intent = new Intent();
            intent.setAction("com.and2long.audio_play_service");
            intent.putExtra("playPosition", currentPosition);
            intent.putExtra("playState", isPlaying());
            sendBroadcast(intent);
        }

        public boolean isPlaying() {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        }


        /**
         * 获取当前音乐播放的进度
         *
         * @return
         */
        public long getProgress() {
            return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
        }

        public long getDuration() {
            return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
        }

        public void seekTo(long progress) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo((int) progress);
            }
        }


        /**
         * 切换播放模式
         */
        /*public void switchPlayMode(){
            switch (playMode) {
                case MODE_ORDER:
                    playMode = MODE_SINGLE_REPEAT;
                    break;
                case MODE_SINGLE_REPEAT:
                    playMode = MODE_ALL_REPEAT;
                    break;
                case MODE_ALL_REPEAT:
                    playMode = MODE_ORDER;
                    break;
            }
            savePlayModeToSp();
        }

        *//**
         * 获取当前播放模式
         * @return
         *//*
        public int getPlayMode(){
            return playMode;
        }*/
    }


    /**
     * 发送状态栏通知
     */
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_icon)
                .setTicker("正在播放")
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        startForeground(1, builder.build());
    }

}
