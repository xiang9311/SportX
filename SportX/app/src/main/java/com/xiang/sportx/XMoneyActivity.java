package com.xiang.sportx;

import android.os.Bundle;
import android.view.View;

import com.xiang.view.MyTitleBar;

public class XMoneyActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_xmoney);
        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myTitleBar.setTitle("我的X币");
        myTitleBar.setMoreButton(0, false, null);
    }
}
