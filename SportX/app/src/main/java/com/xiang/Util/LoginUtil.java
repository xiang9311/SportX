package com.xiang.Util;

import android.content.Context;
import android.util.Log;

import com.xiang.handler.GetRongyunHandler;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Token;
import com.xiang.request.RequestUtil;
import com.xiang.thread.GetRongyunTokenThread;

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

        connectRongyun(new GetRongyunHandler());
    }

    public static void connectRongyun(final GetRongyunHandler handler){
        /**
         * 连接融云
         */
        RongIM.connect(UserStatic.rongyunToken, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

                UserStatic.tryTimes ++;
                if(UserStatic.tryTimes > 3){
                    return ;
                }

                Log.d("rongyun", "onTokenIncorrect");
                long currentMills = System.currentTimeMillis();
                int cmdid = 11002;
                Token.Request11002 request = new Token.Request11002();
                Token.Request11002.Params params = new Token.Request11002.Params();
                Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
                request.common = common;
                request.params = params;
                params.oldTokenCannotUse = true;
                new GetRongyunTokenThread(handler, request).start();
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

    /**
     * 登录到页面后是否跳转到消息页面（从推送打开app）
     */
    public static boolean GotoMessageWhenStart = false;
}
