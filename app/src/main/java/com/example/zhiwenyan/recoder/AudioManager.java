package com.example.zhiwenyan.recoder;


import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AudioManager {
    private static AudioManager mInstance;
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    protected AudioStateListener mAudioStateListener;
    private boolean isPrepare = false;


    public AudioManager(String mDir) {
        this.mDir = mDir;
    }

    /**
     * 回调准备完毕
     *
     * @param mAudioStateListener
     */
    protected void setAudioStateListener(AudioStateListener mAudioStateListener) {
        this.mAudioStateListener = mAudioStateListener;

    }

    public static AudioManager getInstance(String mDir) {
        if (null == mInstance) {
            synchronized (AudioManager.class) {
                mInstance = new AudioManager(mDir);
            }
        }
        return mInstance;
    }

    protected void prepareAudio() {
        isPrepare = false;
        File dir = new File(mDir);
        if (!dir.exists())
            dir.mkdir();
        String filename = getGenerateFileName();
        File file = new File(dir, filename);
        mCurrentFilePath = file.getAbsolutePath();
        try {
            mMediaRecorder = new MediaRecorder(); //设置MediaRecorder
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频的格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepare = true;
            if (mAudioStateListener != null) {
                mAudioStateListener.wellPrepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    private String getGenerateFileName() {
        return UUID.randomUUID() + ".amr";
    }

    protected int getVoiceLevel(int maxLevel) {
        if (isPrepare && mMediaRecorder != null) {
            //1-32767;
            return (int) (maxLevel * mMediaRecorder.getMaxAmplitude() / 32768.0) + 1;
        }
        return 1;
    }

    protected void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    protected void cancel() {
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
        release();
    }

    protected interface AudioStateListener {
        void wellPrepare();
    }

}
