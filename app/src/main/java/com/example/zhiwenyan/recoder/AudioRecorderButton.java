package com.example.zhiwenyan.recoder;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {
    public static final int STATE_NORMAL = 1;
    public static final int STATE_RECORDING = 2;
    public static final int STATE_CANCEL = 3;
    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;
    private DialogManager manager;
    private AudioManager mAudioManager;
    private float mTime;
    //是否触发Ready
    private boolean mReady = false;


    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        manager = new DialogManager(getContext());
        String dir = Environment.getExternalStorageDirectory() + "/imooc_audio";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    public interface onAudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private onAudioFinishRecorderListener mOnAudioFinishRecorderListener;

    protected void setOnAudioFinishRecorderListener(onAudioFinishRecorderListener mOnAudioFinishRecorderListener) {
        this.mOnAudioFinishRecorderListener = mOnAudioFinishRecorderListener;
    }

    public static final int MSG_AUDIO_PREPARED = 0x1;
    public static final int MSG_VOICE_CHANGED = 0x2;
    public static final int MSG_DIALOG_DISMISS = 0x3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    manager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    manager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    manager.dissmissDialog();
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
            }
        }
    };

    //在AudioRecorderButton里面mHandler的MSG_AUDIO_PREPARED处理上，
//    是直接new出的一个线程，在录音停止时也没有remove掉这个线程，所以我在真机上测试时，
//    按下按钮松开后会报错，因为那个线程没有被关闭而还去执行了AudioManager的getVoiceLevel()方法，
//    而此时getVoiceLevel（）方法里面的mMediaRecorder是为null的，所以报了空指针异常。
//    目前我的一个粗暴解决办法是直接在getVoiceLevel()方法里的if里去判断下mMediaRecorder != null。
    @Override
    public void wellPrepare() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (toCancel(x, y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (mTime < 0.6f || !isRecording) {
                    manager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1000);
                } else if (mCurrentState == STATE_RECORDING) {  //正常录制结束
                    manager.dissmissDialog();
                    mAudioManager.release();
                    if (mOnAudioFinishRecorderListener != null) {
                        mOnAudioFinishRecorderListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                } else if (mCurrentState == STATE_CANCEL) {  //取消录音
                    manager.dissmissDialog();
                    mAudioManager.cancel();
                }

                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        mTime = 0;
        mReady = false;
        isRecording = false;
        changeState(STATE_NORMAL);
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn1);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        manager.recording();
                    }
                    break;
                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn1);
                    setText(R.string.str_recorder_cancel);
                    manager.wantToCancel();
                    break;
            }
        }

    }

    private boolean toCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -50 || y > getHeight() + 50) {
            return true;
        }
        return false;
    }
}
