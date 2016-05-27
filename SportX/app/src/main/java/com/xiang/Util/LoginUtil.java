package com.xiang.Util;

import android.content.Context;
import android.util.Log;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by 祥祥 on 2016/5/24.
 */
public class LoginUtil {
    public static void logInSuc(Context context){
        /**
         * 设置jpush
         */
        JPushInterface.setAlias(context, UserStatic.userId + "", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d("gotResult", "gotResult:" + i + s);
            }
        });
        /**
         * 连接融云
         */
        RongIM.connect(UserStatic.rongyunToken, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.d("rongyun", "onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                // log
                Log.d("rongyun", "connect success");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d("rongyun", "onError" + errorCode.getMessage());
            }
        });
    }
}
