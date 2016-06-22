package com.xiang.sportx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiang.Util.Constant;
import com.xiang.Util.UserStatic;
import com.xiang.view.MyTitleBar;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import me.drakeet.materialdialog.MaterialDialog;

public class SettingActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RelativeLayout rl_clean, rl_logout, rl_score, rl_about;
    private TextView tv_cache_size;
    private MaterialDialog md_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setting);

        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        rl_clean = (RelativeLayout) findViewById(R.id.rl_clean);
        rl_logout = (RelativeLayout) findViewById(R.id.rl_logout);
        rl_score = (RelativeLayout) findViewById(R.id.rl_score);
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButtonDefault();
        myTitleBar.setTitle("设置");
        myTitleBar.setMoreButton(0, false, null);

        tv_cache_size.setText((int) (Math.random() * 10) + "M");

        rl_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO clean cache
            }
        });

        rl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO logout
                if ( ! UserStatic.logged){
                    sendToast("您还未登录");
                    return ;
                }

                UserStatic.logged = false;
                UserStatic.userId = 0;
                sendToast("注销成功");
                RongIM.getInstance().disconnect();
                JPushInterface.setAlias(SettingActivity.this, "", new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        Log.d("gotResult", "gotResult:" + i + s);
                    }
                });
                // 发送修改个人界面的广播
                Intent intent = new Intent(Constant.BROADCAST_UPDATE_USERINFO);
                LocalBroadcastManager.getInstance(SettingActivity.this).sendBroadcast(intent);
                finish();
            }
        });

        rl_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (md_about == null){
                    md_about = new MaterialDialog(SettingActivity.this);
                    md_about.setTitle("关于SportX");
                    md_about.setMessage("SportX是一款以运动场地为地理中心的健身运动社交应用。\n" +
                            "联系我们：sportx-park.com。");
                    md_about.setCanceledOnTouchOutside(true);
                    md_about.setPositiveButton("我知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            md_about.dismiss();
                        }
                    });
                }
                md_about.show();
            }
        });
    }
}
