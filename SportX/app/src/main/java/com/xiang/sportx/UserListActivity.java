package com.xiang.sportx;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.UserInfoUtil;
import com.xiang.adapter.UserItemAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.database.helper.BriefUserHelper;
import com.xiang.proto.nano.Common;
import com.xiang.thread.GetMyFensiThread;
import com.xiang.thread.GetMyGuanzhuThread;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RecyclerView recyclerView;

    // adapter
    private UserItemAdapter userItemAdapter;

    // data
    private List<Common.BriefUser> briefUserList = new ArrayList<>();
    private String username = "";
    private int relatedUserId;
    private int findWhat;      // 粉丝还是关注

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
        relatedUserId = getIntent().getIntExtra(Constant.USER_ID, 0);
        username = getIntent().getStringExtra(Constant.USER_NAME);
        findWhat = getIntent().getIntExtra(Constant.FIND_WHAT, Constant.FIND_GUANZHU);
    }

    @Override
    protected void configView() {
        myTitleBar.setBackButtonDefault();
        myTitleBar.setMoreButton(0, false, null);

        StringBuilder sb = new StringBuilder(username);
        if (findWhat == Constant.FIND_FENSI){
            sb.append("的粉丝");
        } else{
            sb.append("的关注");
        }
        myTitleBar.setTitle(sb.toString());

        userItemAdapter = new UserItemAdapter(this, briefUserList, recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, UserItemAdapter.SPAN_COUNT);

        recyclerView.setAdapter(userItemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        mHandler = new MyHandler(this, null);

        if (findWhat == Constant.FIND_FENSI){
            new GetMyFensiThread(mHandler, relatedUserId).start();
        } else{
            new GetMyGuanzhuThread(mHandler, relatedUserId).start();
        }
    }

    private MyHandler mHandler;
    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_FENSI_SUC:
                case KEY_GET_GUANZHU_SUC:
                    Common.BriefUser[] briefUsers = (Common.BriefUser[]) msg.obj;
                    briefUserList.addAll(ArrayUtil.Array2List(briefUsers));
                    userItemAdapter.notifyDataSetChanged();

                    BriefUserHelper briefUserHelper = new BriefUserHelper(UserListActivity.this);
                    for (int i = 0 ; i < briefUsers.length; i ++){
                        Common.BriefUser briefUser = briefUsers[i];
                        UserInfoUtil.updateSavedUserInfo(briefUser.userId, briefUser.userName, briefUser.userAvatar, briefUserHelper);
                    }
                    break;
            }
        }
    }
}
