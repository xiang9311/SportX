package com.xiang.sportx;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiang.view.MyTitleBar;

public class SettingActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RelativeLayout rl_clean, rl_logout;
    private TextView tv_cache_size;


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

        tv_cache_size.setText((int)(Math.random() * 10) + "M");

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
            }
        });
    }
}
