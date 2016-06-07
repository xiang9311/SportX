package com.xiang.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xiang.Util.Constant;
import com.xiang.Util.LoginUtil;
import com.xiang.Util.SystemUtil;
import com.xiang.sportx.CommentMessageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 祥祥 on 2016/5/24.
 */
public class MyJpushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())){
            // 自定义的消息通知
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())){
            // 收到通知
            if(bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE).equals("新消息")) {
                try {
                    JSONObject extra = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//                    int trendId = Integer.parseInt(extra.getString("trendId"));
                    Intent bIntent = new Intent(Constant.BROADCAST_NEW_COMMENT_MESSAGE);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(bIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        } else if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            if(bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE).equals("新消息")) {
                Intent bIntent = new Intent(Constant.BROADCAST_GOTO_MESSAGE_ACTIVITY);
                LocalBroadcastManager.getInstance(context).sendBroadcast(bIntent);

                //判断app进程是否存活
                if(SystemUtil.isAppAlive(context, "com.xiang.sportx")){
                    //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
                    Intent detailIntent = new Intent(context, CommentMessageActivity.class);
                    detailIntent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(detailIntent);
                }else {
                    //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
                    //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入
                    //参数跳转到DetailActivity中去了
                    Log.i("NotificationReceiver", "the app process is dead");
                    LoginUtil.GotoMessageWhenStart = true;
                    Intent launchIntent = context.getPackageManager().
                            getLaunchIntentForPackage("com.xiang.sportx");
                    launchIntent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    context.startActivity(launchIntent);
                }
            }
        }
    }
}
