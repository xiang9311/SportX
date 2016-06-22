package com.xiang.handler;

import android.os.Handler;
import android.os.Message;

import com.xiang.Util.LoginUtil;
import com.xiang.Util.UserStatic;

/**
 * Created by 祥祥 on 2016/6/21.
 */
public class GetRongyunHandler extends Handler {
    public static final int KEY_GET_RONGYUN_TOKEN_SUC = 101;
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case KEY_GET_RONGYUN_TOKEN_SUC:
                UserStatic.rongyunToken = (String) msg.obj;
                LoginUtil.connectRongyun(this);
                break;
            default:
                break;
        }
    }
}
