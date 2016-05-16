package com.xiang.sportx;

import android.os.Bundle;

import com.xiang.view.MyTitleBar;

public class ConversationActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_conversation);

        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setTitle("");
        myTitleBar.setBackButtonDefault();
        myTitleBar.setMoreButton(0, false, null);
    }
}
