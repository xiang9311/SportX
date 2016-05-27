package com.xiang.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.xiang.Util.Constant;

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
            }
        }
    }
}
