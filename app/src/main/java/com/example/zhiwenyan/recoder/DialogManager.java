package com.example.zhiwenyan.recoder;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class DialogManager {
    private Dialog mDialog;
    private ImageView mIcon;
    private ImageView mAudio;
    private TextView text;
    private Context mContext;


    public DialogManager(Context mContext) {
        this.mContext = mContext;



    }

    protected void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.AudioDialogTheme);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog, null);
        mDialog.setContentView(contentView);

        mIcon = (ImageView) mDialog.findViewById(R.id.img1);
        mAudio = (ImageView) mDialog.findViewById(R.id.img2);
        text = (TextView) mDialog.findViewById(R.id.text1);
        mDialog.show();

    }

    protected void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mAudio.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.cancel);
            text.setText("松开手指,取消发送");
        }

    }

    protected void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mAudio.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.voice_to_short);
            text.setText("录音时间过短");
        }
    }

    protected void dissmissDialog() {

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 正在录音
     */
    protected void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mAudio.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            text.setText("手指向上滑动,取消发送");
        }
    }

    /**
     *
     * @param level
     */
    protected void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
//            mIcon.setVisibility(View.VISIBLE);
//            mAudio.setVisibility(View.VISIBLE);
//            text.setVisibility(View.VISIBLE);
            int resId = mContext.getResources().getIdentifier("v" + level,
                    "drawable", mContext.getPackageName());
            mAudio.setImageResource(resId);
        }
    }
}
