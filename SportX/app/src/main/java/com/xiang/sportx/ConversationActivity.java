package com.xiang.sportx;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.xiang.Util.SportXIntent;
import com.xiang.view.MyTitleBar;

public class ConversationActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;

    // data
    private String chatUserid;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_conversation);

        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        Uri uri = getIntent().getData();
        userName = uri.getQueryParameter("title");
        chatUserid = uri.getQueryParameter("targetId");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setTitle(userName);
        myTitleBar.setBackButtonDefault();
        myTitleBar.setMoreButtonPadding(10);
        myTitleBar.setMoreButton(R.mipmap.user_white, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SportXIntent.gotoUserDetail(ConversationActivity.this, Integer.parseInt(chatUserid), userName);
                } catch (Exception e){

                }
            }
        });
    }
}
