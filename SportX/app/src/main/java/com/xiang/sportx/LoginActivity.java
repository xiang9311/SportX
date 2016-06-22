package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.sea_monster.common.Md5;
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
import com.xiang.view.MyTitleBarLight;

public class LoginActivity extends BaseAppCompatActivity {

    private MyTitleBarLight titleBar;
    private MaterialEditText met_phone, met_password;
    private ActionProcessButton apb_login, apb_register;
    private CheckBox cb_auto_login;                 // 自动登录
    private CheckBox cb_remember_password;          //记住密码

    // tools
    private SharedPreferences sp;
    private BriefUserHelper briefUserHelper = new BriefUserHelper(LoginActivity.this);

    private int from;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String phone = intent.getStringExtra(Constant.PHONE);
        String password = intent.getStringExtra(Constant.PASSWORD);

        met_password.setText(password);
        met_phone.setText(phone);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        from = getIntent().getIntExtra(Constant.FROM, -1);

        titleBar = (MyTitleBarLight) findViewById(R.id.titleBar);
        met_phone = (MaterialEditText) findViewById(R.id.met_phone);
        met_password = (MaterialEditText) findViewById(R.id.met_password);
        apb_login = (ActionProcessButton) findViewById(R.id.apb_login);
        apb_register = (ActionProcessButton) findViewById(R.id.apb_register);
        cb_auto_login = (CheckBox) findViewById(R.id.cb_auto_login);
        cb_remember_password = (CheckBox) findViewById(R.id.cb_remember_password);

        colorId = R.color.gray_background;
    }

    @Override
    protected void initData() {
        sp = getSharedPreferences(Constant.SP_DATA, MODE_PRIVATE);

    }

    @Override
    protected void configView() {
        titleBar.setDefault("登录");
        if (from == Constant.FROM_LAUNCH){
            titleBar.setMoreTextButton("跳过", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, MainPagerActivity.class));
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constant.NOT_LOG, true);
                    editor.commit();
                }
            });
        }
        apb_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        apb_login.setMode(ActionProcessButton.Mode.ENDLESS);
        apb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apb_login.setProgress(50);
                apb_login.setEnabled(false);
                apb_register.setEnabled(false);
                met_password.setEnabled(false);
                met_phone.setEnabled(false);
                cb_auto_login.setEnabled(false);
                cb_remember_password.setEnabled(false);
                new LoginThread().start();
            }
        });

        met_phone.addTextChangedListener(tw_for_LoginButton);

        met_password.addTextChangedListener(tw_for_LoginButton);

        cb_remember_password.setChecked(true);
        cb_auto_login.setChecked(true);
        cb_auto_login.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_remember_password.setChecked(true);
                }
            }
        });

        // 是否有记住密码
        if(sp.getBoolean(Constant.REMEMBER, false)){
            met_password.setText("");
            met_password.setText(sp.getString(Constant.PASSWORD, ""));
            met_phone.setText(sp.getString(Constant.LOGIN_USER_NAME, ""));
        }

        mHandler = new MyHandler(this, null);
    }

    private TextWatcher tw_for_LoginButton = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLoginButtonState();
        }
    };

    /**
     * 更新登录按钮的状态
     */
    private void updateLoginButtonState(){
        if (met_password.getText().toString().equals("") || met_phone.getText().toString().equals("")){
            apb_login.setEnabled(false);
        } else{
            apb_login.setEnabled(true);
        }
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
            switch (msg.what){
                case KEY_LOGIN_SUC:

                    briefUserHelper.saveUser(new TblBriefUser(UserStatic.userId+"", UserStatic.realUserName, UserStatic.avatarUrl));

                    SharedPreferences.Editor editor = sp.edit();
                    if(cb_remember_password.isChecked()){
                        // 记住密码
                        editor.putBoolean(Constant.REMEMBER, true);
                        editor.putString(Constant.LOGIN_USER_NAME, UserStatic.loginUserName);
                        editor.putString(Constant.PASSWORD, UserStatic.psssword);
                    }
                    if(cb_auto_login.isChecked()){
                        // 自动登录
                        editor.putBoolean(Constant.AUTO_LOGIN, true);
                    }
                    editor.commit();

                    LoginUtil.logInSuc(LoginActivity.this);

                    if(from == Constant.FROM_LAUNCH){
                        startActivity(new Intent(LoginActivity.this, MainPagerActivity.class));
                    }

                    Intent intent = new Intent(Constant.BROADCAST_UPDATE_USERINFO);
                    LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(intent);
                    finish();
                    break;

                case KEY_NO_RES:
                case KEY_PARSE_ERROR:
                case KEY_ERROR:
                case KEY_LOGIN_ERROR:
                    met_password.setEnabled(true);
                    met_phone.setEnabled(true);
                    apb_login.setEnabled(true);
                    apb_register.setEnabled(true);
                    apb_login.setProgress(0);
                    sendToast("登录失败，请重试");
                    break;
            }
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

            String phone = met_phone.getText().toString();
            String password = Md5.encode(met_password.getText().toString()).toUpperCase();

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
