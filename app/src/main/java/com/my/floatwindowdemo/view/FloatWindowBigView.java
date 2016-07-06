package com.my.floatwindowdemo.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.my.floatwindowdemo.R;
import com.my.floatwindowdemo.service.FloatWindowService;
import com.my.floatwindowdemo.service.MyWindowManager;

/**
 * Created by lzh on 2016/7/6.
 */
public class FloatWindowBigView extends LinearLayout{

    /**
     * record width of big float window
     */
    public static int mViewWidth;

    /**
     * record height of big float window
     */
    public static int mViewHeight;

    public FloatWindowBigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_float_window);
        mViewWidth = view.getLayoutParams().width;
        mViewHeight = view.getLayoutParams().height;
        Button close = (Button) view.findViewById(R.id.close_btn);
        Button back =(Button) view.findViewById(R.id.back);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(), FloatWindowService.class);
                context.stopService(intent);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
    }
}
