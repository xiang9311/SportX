package com.xiang.sportx;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xiang.adapter.UserItemAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;

import java.util.List;

public class UserListActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RecyclerView recyclerView;

    // adapter
    private UserItemAdapter userItemAdapter;

    // data
    private List<Common.BriefUser> briefUserList = DefaultUtil.getBriefUsers((int) (Math.random() * 60));

    // tools

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_user_list);

        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_users);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButtonDefault();
        myTitleBar.setTitle("用户列表");  // TODO
        myTitleBar.setMoreButton(0, false, null);

        userItemAdapter = new UserItemAdapter(this, briefUserList, recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, UserItemAdapter.SPAN_COUNT);

        recyclerView.setAdapter(userItemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }
}
