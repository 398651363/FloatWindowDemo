package com.my.floatwindowdemo.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lzh on 2016/6/29.
 */
public class FloatWindowService extends Service{

    private final String TAG = "FloatWindowService";
    private final int RUNNING_ITEM_MAX_NUM = 100;
    private PackageManager mPm;
    private ActivityManager mAm;

    /**
     * used for creating or removing float window in thread
     */
    private Handler mHandler = new Handler();

    /**
     * used for checking whether to creating or removing float window on time
     */
    private Timer mTimer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mPm = getPackageManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class RefreshTask extends TimerTask {
        @Override
        public void run() {
            if (isHome() && !MyWindowManager.isWindowShowing()) {   // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("lyh", "show small window !!!");
//                        MyWindowManager.createSmallWindow(getApplicationContext());
                        MyWindowManager.creatBigWindow(getApplicationContext());
                    }
                });
            } else if (!isHome() && MyWindowManager.isWindowShowing()) {    // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("lyh", "close all window !!!");
                        MyWindowManager.removeSmallWindow(getApplicationContext());
                        MyWindowManager.removeBigWindow(getApplicationContext());
                    }
                });
            } else if (isHome() && MyWindowManager.isWindowShowing()) {    // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("lyh", "update percent !!!");
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }
        }
    }

    private boolean isHome() {
        boolean isHome = false;
        String topRunningTaskPkgName = getRunningTaskInfos(mAm, 1).get(0).topActivity.getPackageName();
        String usingHomePkgName = getUsingHome(mPm);
        if (usingHomePkgName.equals(topRunningTaskPkgName)) {
            isHome = true;
        }
        Log.i(TAG, "topRunningTaskPkgName is : " + topRunningTaskPkgName + " ; usingHomePkgName is : " + usingHomePkgName);
        return isHome;
    }

    private String getUsingHome(PackageManager packageManager) {
        String pkgName = null;
        String homeName = null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);
        if (resolveInfo.activityInfo != null) {
            pkgName = resolveInfo.activityInfo.packageName;
            if ("android".equals(pkgName)) {
                Log.i(TAG, "there are more than one launchers !");
                List<String> allHomes = getAllHomes(packageManager);
                List<ActivityManager.RunningTaskInfo> runningTaskInfos = getRunningTaskInfos(mAm, RUNNING_ITEM_MAX_NUM);
                for (ActivityManager.RunningTaskInfo info : runningTaskInfos) {
                    String currentPkgName = info.topActivity.getPackageName();
                    if (allHomes.contains(currentPkgName)) {
                        homeName = currentPkgName;
                        break;
                    }
                }
            } else {
                homeName = pkgName;
            }
        }
        return homeName;
    }

    private List<String> getAllHomes(PackageManager packageManager) {
        List<String> homeNames = new ArrayList<String>();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            homeNames.add(resolveInfo.activityInfo.packageName);
        }
        return homeNames;
    }

    private List<ActivityManager.RunningTaskInfo> getRunningTaskInfos(ActivityManager activityManager, int maxNum) {
        return activityManager.getRunningTasks(maxNum);
    }
}
