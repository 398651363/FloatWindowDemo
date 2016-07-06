package com.my.floatwindowdemo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.floatwindowdemo.R;
import com.my.floatwindowdemo.service.MyWindowManager;

import java.lang.reflect.Field;

/**
 * Created by lzh on 2016/7/6.
 */
public class FloatWindowSmallView extends LinearLayout{

    /**
     * record width of small float window
     */
    public static int mViewWidth;

    /**
     * record height of small float window
     */
    public static int mViewHeight;

    /**
     * record height of status bar
     */
    private static int mStatusBarHeight;

    /**
     * used for updating location of float window
     */
    private WindowManager mWindowManager;

    /**
     * params of float window
     */
    private WindowManager.LayoutParams mParams;

    /**
     * record current X location of finger 记录当前手指位置在屏幕上的横坐标值
     */
    private float mXInScreen;

    /**
     * record current Y location of finger 记录当前手指位置在屏幕上的纵坐标值
     */
    private float mYInScreen;

    /**
     * record current X location of finger when pressing down 记录手指按下时在屏幕上的横坐标的值
     */
    private float mXDownInScreen;

    /**
     * record current Y location of finger when pressing down 记录手指按下时在屏幕上的纵坐标的值
     */
    private float mYDownInScreen;

    /**
     * record current X location in screen of float window when pressing down 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float mXInView;

    /**
     * record current Y location in screen of float window when pressing down 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float mYInView;

    public FloatWindowSmallView(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_float_window);
        mViewWidth = view.getLayoutParams().width;
        mViewHeight = view.getLayoutParams().height;
        TextView percentView = (TextView) view.findViewById(R.id.percent);
        percentView.setText(MyWindowManager.getUsedPercentValueOfRam());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                mXInView = event.getX();
                mYInView = event.getY();
                mXDownInScreen = event.getRawX();
                mYDownInScreen = event.getRawY() - getHeightOfStatusBar();
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY() - getHeightOfStatusBar();
                break;
            case MotionEvent.ACTION_MOVE:
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY() - getHeightOfStatusBar();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (mXDownInScreen == mXInScreen && mYDownInScreen == mYInScreen) {
                    openBigWindow();
                }
                break;
        }
        return true;
    }

    /**
     * used for updating the positon of float window
     * @param params of float window
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * updating postion of float window
     */
    private void updateViewPosition() {
        mParams.x = (int)(mXInScreen - mXInView);
        mParams.y = (int)(mYInScreen - mYInView);
        mWindowManager.updateViewLayout(this, mParams);
    }

    /**
     * open big window and close small window
     */
    private void openBigWindow() {
        MyWindowManager.creatBigWindow(getContext());
        MyWindowManager.removeSmallWindow(getContext());
    }

    /**
     * get height of status bar
     */
    private int getHeightOfStatusBar() {
        if (mStatusBarHeight == 0){
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                mStatusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mStatusBarHeight;
    }
}
