package com.xiang.base;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.xiang.factory.MaterialDialogFactory;
import com.xiang.sportx.BaseAppCompatActivity;
import com.xiang.view.TwoOptionMaterialDialog;

/**
 * Created by 祥祥 on 2016/3/21.
 */
public class BaseHandler extends Handler {
    private static final String TAG = "BaseHandler";
    protected Context context;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected Snackbar snackbar;

    public BaseHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout){
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public static final int KEY_TOAST = 1000;
    public static final int KEY_TOAST_LONG = 1001;
    public static final int KEY_DISSMISS_PROGRESS = 1002;
    public static final int KEY_NEED_LOGGED = 1003;
    public static final int KEY_ERROR = 2001;
    public static final int KEY_SUCCESS = 2000;
    public static final int KEY_NO_RES = 2002;
    public static final int KEY_PARSE_ERROR = 2003;

    /**
     * 获取七牛token成功是发送该消息
     */
    public static final int KEY_GET_QINIU_TOKEN_SUC = 3001;
    /**
     * 给trend 点赞
     */
    public static final int KEY_LIKE_TREND = 3002;
    /**
     * 给trend 取消点赞
     */
    public static final int KEY_UNLIKE_TREND = 3003;
    /**
     * 获取用户的trend
     */
    public static final int KEY_GET_TREND_LIST_SUC = 3004;
    /**
     * 获取用户粉丝
     */
    public static final int KEY_GET_FENSI_SUC = 3005;
    /**
     * 获取用户关注
     */
    public static final int KEY_GET_GUANZHU_SUC = 3006;
    /**
     * 获取用户简要信息
     */
    public static final int KEY_GET_BRIEFUSER_SUC = 3007;
    /**
     * 获取gym列表成功
     */
    public static final int KEY_GET_GYM_LIST_SUC = 3008;
    /**
     * 获取用户简要信息
     */
    public static final int KEY_GET_RECOMMENDUSER_SUC = 3009;

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
                    Toast.makeText(context, "无法连接到服务器", Toast.LENGTH_SHORT).show();
                }
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case KEY_TOAST:
                if (context != null){
                    if(snackbar == null) {
                        snackbar = Snackbar.make(((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content)
                                , (String) msg.obj
                                , Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else{
                        snackbar.setText((String) msg.obj);
                        snackbar.show();
                    }
                }
                break;
            case KEY_TOAST_LONG:
                if (context != null){
                    Snackbar.make(((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content)
                            , (String) msg.obj
                            , Snackbar.LENGTH_LONG).show();
                }
                break;
            case KEY_DISSMISS_PROGRESS:
                try {
                    ((BaseAppCompatActivity) context).dissmissProgress();
                } catch(Exception e){

                }
                break;

            case KEY_NEED_LOGGED:
                TwoOptionMaterialDialog md_login_register = MaterialDialogFactory.createLoginOrRegisterMd(context);
                md_login_register.show();
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

    public void sendDisMissProgress(){
        Message msg = Message.obtain();
        msg.what = KEY_DISSMISS_PROGRESS;
        this.sendMessage(msg);
    }
}
