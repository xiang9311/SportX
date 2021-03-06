package com.xiang.Util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/25.
 */
public class ActivityUtil {
    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//                    Log.i("后台", appProcess.processName);
                    return false;
                }else{
//                    Log.i("前台", appProcess.processName);
                    return true;
                }
            }
        }
        return false;
    }
}
