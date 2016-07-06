package com.my.floatwindowdemo.service;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.my.floatwindowdemo.R;
import com.my.floatwindowdemo.view.FloatWindowBigView;
import com.my.floatwindowdemo.view.FloatWindowSmallView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by lzh on 2016/7/6.
 */
public class MyWindowManager {
    private static FloatWindowSmallView mSmallWindow;
    private static FloatWindowBigView mBigWindow;
    private static WindowManager.LayoutParams mSmallWindowParams;
    private static WindowManager.LayoutParams mBigWindowParams;
    private static WindowManager mWindowManager;

    public static void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeigth = windowManager.getDefaultDisplay().getHeight();
        if (mSmallWindow == null) {
            mSmallWindow = new FloatWindowSmallView(context);
            if (mSmallWindowParams == null) {
                mSmallWindowParams = new WindowManager.LayoutParams();
                mSmallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mSmallWindowParams.format = PixelFormat.RGBA_8888;
                mSmallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mSmallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                mSmallWindowParams.width = FloatWindowSmallView.mViewWidth;
                mSmallWindowParams.height = FloatWindowSmallView.mViewHeight;
                mSmallWindowParams.x = screenWidth;
                mSmallWindowParams.y = screenHeigth / 2;
            }
            mSmallWindow.setParams(mSmallWindowParams);
            windowManager.addView(mSmallWindow, mSmallWindowParams);
        }
    }

    public static void removeSmallWindow(Context context) {
        if (mSmallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mSmallWindow);
            mSmallWindow = null;
        }
    }

    public static void creatBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (mBigWindow == null) {
            mBigWindow = new FloatWindowBigView(context);
            if (mBigWindowParams == null) {
                mBigWindowParams = new WindowManager.LayoutParams();
                mBigWindowParams.x = (screenWidth - FloatWindowBigView.mViewWidth) / 2;
                mBigWindowParams.y = (screenHeight - FloatWindowBigView.mViewHeight) / 2;
                mBigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mBigWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mBigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                mBigWindowParams.width = FloatWindowBigView.mViewWidth;
                mBigWindowParams.height = FloatWindowBigView.mViewHeight;
            }
            windowManager.addView(mBigWindow, mBigWindowParams);
        }
    }

    public static void removeBigWindow(Context context) {
        if (mBigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mBigWindow);
            mBigWindow = null;
        }
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null){
            mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static boolean isWindowShowing() {
        return mSmallWindow != null || mBigWindow != null;
    }

    public static String getUsedPercentValueOfRam() {
        String totalMemory = null;
        String availableMemory = null;
        String[] totalMemoryArray = null;
        String[] availableMemoryArray = null;
        long freeSize = 0;
        long totalSize = 0;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String percentString = "";

        try {
            fileReader = new FileReader("/proc/meminfo");
            bufferedReader = new BufferedReader(fileReader, 4096);
            // get total memory info
            totalMemory = bufferedReader.readLine();
            totalMemoryArray = totalMemory.split("\\s+");
            totalSize = Long.valueOf(totalMemoryArray[0]).longValue() / 1024;
            // get free memory info
            for (int i = 0; i < 3; i++) {
                availableMemory = bufferedReader.readLine();
                availableMemoryArray = availableMemory.split("\\s+");
                freeSize += Long.valueOf(availableMemoryArray[0]).longValue();
            }
            freeSize = freeSize / 1024;
            percentString = ((totalSize - freeSize) / (float)totalSize) * 100 + "%";
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (fileReader != null)
                    fileReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return percentString;
    }

    public static void updateUsedPercent(Context context) {
        if (mSmallWindow != null) {
            TextView percentView = (TextView) mSmallWindow.findViewById(R.id.percent);
            percentView.setText(getUsedPercentValueOfRam());
        }
    }
}
