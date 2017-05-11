package com.example.zhiwenyan.recoder;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;


public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause = false;
    static onCompletionListener mOnCompletionListener;

    protected static void playsound(String path) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mOnCompletionListener.onCompletion();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                return false;
            }
        });
    }

    protected static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            isPause = true;
        }
    }

    protected static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
        }
    }

    protected static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public interface onCompletionListener {
        void onCompletion();
    }

    public static  void setOnCompletionListener(onCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }
}
