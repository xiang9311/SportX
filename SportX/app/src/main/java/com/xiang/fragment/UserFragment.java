package com.xiang.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.Constant;
import com.xiang.Util.SportXIntent;
import com.xiang.Util.UserStatic;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.sportx.LoginActivity;
import com.xiang.sportx.MyDetailActivity;
import com.xiang.sportx.R;
import com.xiang.sportx.RegisterActivity;
import com.xiang.sportx.SettingActivity;
import com.xiang.sportx.UserAlbumActivity;
import com.xiang.sportx.XMoneyActivity;
import com.xiang.view.TwoOptionMaterialDialog;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class UserFragment extends BaseFragment {

    private View mView;
    private ImageView iv_avatar;
    private RelativeLayout rl_photo, rl_user_detail, rl_money, rl_guanzhu, rl_fensi, rl_setting;
    private TextView tv_username, tv_usersign;
    private TwoOptionMaterialDialog md_login_register;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BROADCAST_UPDATE_USERINFO)){
                updateUserInfo();
            }
        }
    };

    /**
     * 用户信息更新后，更新界面内容
     */
    private void updateUserInfo() {
        try{
            initUser();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onInitFragment() {

        Log.d("onInitFragment","onInitFragment");

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        rl_user_detail = (RelativeLayout) findViewById(R.id.rl_user_detail);
        rl_money = (RelativeLayout) findViewById(R.id.rl_money);
        rl_guanzhu = (RelativeLayout) findViewById(R.id.rl_guanzhu);
        rl_fensi = (RelativeLayout) findViewById(R.id.rl_fensi);
        rl_setting = (RelativeLayout) findViewById(R.id.rl_setting);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_usersign = (TextView) findViewById(R.id.tv_user_sign);

        initUser();


        rl_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserAlbumActivity.class));
            }
        });


        rl_user_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserStatic.logged) {
                    startActivity(new Intent(getContext(), MyDetailActivity.class));
                } else{
                    if(md_login_register == null){
                        String[] options = new String[]{"登录", "注册"};
                        md_login_register = MaterialDialogFactory.createTwoOptionMd(getContext(), options, false, 0, true);
                        md_login_register.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                            @Override
                            public void onOptionChoose(int index) {
                                if(index == 0){
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                } else{
                                    startActivity(new Intent(getContext(), RegisterActivity.class));
                                }
                            }
                        });
                        md_login_register.setTitle("您还未登录");
                        md_login_register.setCanceledOnTouchOutside(true);
                    }
                    md_login_register.show();
                }
            }
        });

        rl_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), XMoneyActivity.class));
            }
        });

        rl_guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserStatic.logged) {
                    SportXIntent.gotoUserListActivity(getContext(), UserStatic.userId, UserStatic.realUserName, Constant.FIND_GUANZHU);
                }
            }
        });

        rl_fensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserStatic.logged) {
                    SportXIntent.gotoUserListActivity(getContext(), UserStatic.userId, UserStatic.realUserName, Constant.FIND_FENSI);
                }
            }
        });

        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.BROADCAST_UPDATE_USERINFO);
        //注册广播
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        try {
            getContext().unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void initUser() {
        if(UserStatic.logged) {
            imageLoader.displayImage(UserStatic.avatarUrl, iv_avatar, options);
            tv_username.setText(UserStatic.realUserName);
            if (UserStatic.sign == null || UserStatic.sign.equals("")) {
                tv_usersign.setText("一句话介绍自己");
            } else {
                tv_usersign.setText(UserStatic.sign);
            }
        } else{
            tv_username.setText("未登录");
            tv_usersign.setText("");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_user, container, false);
        return mView;
    }

    private View findViewById(int id) {
        return mView.findViewById(id);
    }
}
