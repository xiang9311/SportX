package com.xiang.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 祥祥 on 2016/3/21.
 */
public class BaseHandler extends Handler {
    private static final String TAG = "BaseHandler";
    protected Context context;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    public BaseHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout){
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public static final int KEY_TOAST = 1000;
    public static final int KEY_TOAST_LONG = 1001;
    public static final int KEY_ERROR = 2001;
    public static final int KEY_SUCCESS = 2000;
    public static final int KEY_NO_RES = 2002;
    public static final int KEY_PARSE_ERROR = 2003;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Log.d(TAG, "BaseHandler开始及处理");

        switch (msg.what){
            case KEY_ERROR:
                if (context != null){
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case KEY_PARSE_ERROR:
                if (context != null){
                    Toast.makeText(context, "数据出错了，再试试", Toast.LENGTH_SHORT).show();
                }
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case KEY_NO_RES:
                if (context != null){
                    Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case KEY_TOAST:
                if (context != null){
                    Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                }
                break;
            case KEY_TOAST_LONG:
                if (context != null){
                    Toast.makeText(context, (String)msg.obj, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void sendToast(String content){
        Message msg = Message.obtain();
        msg.what = KEY_TOAST;
        msg.obj = content;
        this.sendMessage(msg);
    }

    public void sendToastLong(String content){
        Message msg = Message.obtain();
        msg.what = KEY_TOAST_LONG;
        msg.obj = content;
        this.sendMessage(msg);
    }
}
