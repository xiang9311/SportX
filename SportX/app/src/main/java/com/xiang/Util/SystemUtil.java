package com.xiang.Util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/30.
 */
public class SystemUtil {
    public static boolean isAppAlive(Context context, String packageName){
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        return isAppRunning;
    }
}
