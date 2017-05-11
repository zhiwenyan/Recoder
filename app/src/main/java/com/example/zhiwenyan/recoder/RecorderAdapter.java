package com.example.zhiwenyan.recoder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zhiwenyan on 10/16/16.
 */

public class RecorderAdapter extends ArrayAdapter<Recorder> {
    private ArrayList<Recorder> mArrayList;
    private Context mContext;
    private int mMinItemWidth;
    private int mMaxItemWidth;
    private LayoutInflater mLayoutInflater;

    public RecorderAdapter(Context context, ArrayList<Recorder> datas) {
        super(context, -1, datas);
        this.mContext = context;
        this.mArrayList = datas;
        mLayoutInflater = LayoutInflater.from(mContext);
        WindowManager manager = (WindowManager) mContext.
                getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        mMaxItemWidth = (int) (dm.widthPixels * 0.7f);
        mMinItemWidth = (int) (dm.widthPixels * 0.15f);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_recorder,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mView = convertView.findViewById(R.id.v1);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_recorder_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(Math.round(mArrayList.get(position).getTime()) + "s");
        ViewGroup.LayoutParams params = viewHolder.mView.getLayoutParams();
        params.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * getItem(position).getTime()));
        return convertView;


    }

    class ViewHolder {
        TextView mTextView;
        View mView;

    }

}
