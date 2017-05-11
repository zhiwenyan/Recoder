package com.example.zhiwenyan.recoder;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MediaManager.onCompletionListener {
    private ListView mListView;
    private ArrayList<Recorder> mArrayList = new ArrayList<>();
    private RecorderAdapter mAdapter;
    private Recorder mRecorder;
    private AnimationDrawable animationDrawable;
    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        AudioRecorderButton audioRecorderButton = (AudioRecorderButton) findViewById(R.id.audio_btn);
        audioRecorderButton.setOnAudioFinishRecorderListener(new AudioRecorderButton.onAudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                mRecorder = new Recorder();
                mRecorder.setTime(seconds);
                mRecorder.setPath(filePath);
                mArrayList.add(mRecorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mArrayList.size() - 1);
            }
        });
        mAdapter = new RecorderAdapter(this, mArrayList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //播放音频
                //播放动画
                animView = view.findViewById(R.id.v1);
                animView.setBackgroundResource(R.drawable.play_time);
                animationDrawable = (AnimationDrawable) animView.getBackground();
                animationDrawable.start();
                MediaManager.playsound(mArrayList.get(position).getPath());
                MediaManager.setOnCompletionListener(MainActivity.this);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    @Override
    public void onCompletion() {
        animView.setBackgroundResource(R.drawable.adj);
        animationDrawable.stop();
    }
}
