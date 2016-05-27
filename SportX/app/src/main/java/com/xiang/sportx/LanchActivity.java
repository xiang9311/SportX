package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.xiang.Util.Constant;
import com.xiang.Util.LoginUtil;
import com.xiang.Util.UserStatic;
import com.xiang.base.BaseHandler;
import com.xiang.database.helper.BriefUserHelper;
import com.xiang.database.model.TblBriefUser;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

public class LanchActivity extends BaseAppCompatActivity {

    private static final long MIN_TIME = 3000;       // 页面最低显示时间

    private String phone;
    private String password;
    // tools
    private SharedPreferences sp;

    private long startTime = 0;

    private BriefUserHelper briefUserHelper = new BriefUserHelper(LanchActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTime = System.currentTimeMillis();

        setContentView(R.layout.activity_lanch);
        ImageView iv_loading = (ImageView) findViewById(R.id.fullscreen_content);
        int ran = (int) (Math.random() * 10 % 4);
        int[] drawable = new int[]{R.mipmap.load0, R.mipmap.load1, R.mipmap.load2, R.mipmap.load3};
        iv_loading.setImageDrawable(getResources().getDrawable(drawable[ran]));

        sp = getSharedPreferences(Constant.SP_DATA, MODE_PRIVATE);

        mHandler = new MyHandler(this, null);

        // 是否有记住密码
        if(sp.getBoolean(Constant.REMEMBER, false) && sp.getBoolean(Constant.AUTO_LOGIN, false)){
            password = sp.getString(Constant.PASSWORD, "");
            phone = sp.getString(Constant.LOGIN_USER_NAME, "");
            new LoginThread().start();
        } else{
            // 如果第一次启动，则启动登录，否则，启动mainpager
            if ( ! sp.getBoolean(Constant.NOT_LOG, false)){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LanchActivity.this, LoginActivity.class);
                        intent.putExtra(Constant.FROM, Constant.FROM_LAUNCH);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);

            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LanchActivity.this, MainPagerActivity.class));
                        finish();
                    }
                }, 2000);

            }
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {

    }

    private MyHandler mHandler;
    private static final int KEY_LOGIN_SUC = 101;
    private static final int KEY_LOGIN_ERROR = 102;

    class MyHandler extends BaseHandler {

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            long now = System.currentTimeMillis();
            long last = MIN_TIME - now + startTime;

            switch (msg.what){
                case KEY_LOGIN_SUC:

                    LoginUtil.logInSuc(LanchActivity.this);

                    briefUserHelper.saveUser(new TblBriefUser(UserStatic.userId+"", UserStatic.realUserName, UserStatic.avatarUrl));

                    if(Constant.DEBUG) {
                        sendToast("登录成功");
                    }
                    break ;

                case KEY_LOGIN_ERROR:
                    sendToast("登录失败");
                    break ;

                case KEY_TOAST:
                case KEY_TOAST_LONG:
                    return ;

            }
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LanchActivity.this, MainPagerActivity.class));
                    finish();
                }
            }, last);

            return ;
        }
    }

    /**
     * 登录的线程
     */
    class LoginThread extends Thread{
        public LoginThread(){

        }
        /**
         * Calls the <code>run()</code> method of the Runnable object the receiver
         * holds. If no Runnable is set, does nothing.
         *
         * @see Thread#start
         */
        @Override
        public void run() {
            super.run();

            long currentMills = System.currentTimeMillis();
            int cmdid = 10002;
            Pilot.Request10002 request = new Pilot.Request10002();
            Pilot.Request10002.Params params = new Pilot.Request10002.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.phone = phone;
            params.password = password;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_LOGIN, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10002 response = Pilot.Response10002.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            UserStatic.loginUserName = phone;     // 用户名或密码
                            UserStatic.realUserName = response.data.briefUser.userName;
                            UserStatic.avatarUrl = response.data.briefUser.userAvatar;
                            UserStatic.logged = true;
                            UserStatic.psssword = password;
                            UserStatic.userId = response.data.briefUser.userId;
                            UserStatic.rongyunToken = response.data.rongyunToken;
                            UserStatic.phone = response.data.phone;
                            UserStatic.sex = response.data.sex;
                            UserStatic.sign = response.data.sign;
                            UserStatic.userKey = response.data.userKey;
                            Message msg = Message.obtain();
                            msg.what = KEY_LOGIN_SUC;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = KEY_LOGIN_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }
}
