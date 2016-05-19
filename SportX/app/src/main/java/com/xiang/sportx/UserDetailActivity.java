package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.adapter.TrendAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;

import io.rong.imkit.RongIM;

public class UserDetailActivity extends BaseAppCompatActivity {

    private RecyclerView rv_trend;
    private View headerView;
    private MyTitleBar titleBar;

    // headview中的内容
    private ImageView iv_avatar;
    private TextView tv_username;
    private TextView tv_follow, tv_chat;       // 关注的按钮
    private TextView tv_sign;         // 签名
    private ImageView iv_sex;
    private TextView tv_guanzhu_count, tv_fensi_count, tv_trend_count;
    private LinearLayout ll_guanzhu, ll_fensi;

    // adapter
    private static TrendAdapter trendAdapter;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions = DisplayOptionsFactory.createNormalImageOption();

    // data
    private Common.DetailUser detailUser = DefaultUtil.getDetailUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_user_detail);

        rv_trend = (RecyclerView) findViewById(R.id.rv_user_trend);
        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        initHeadView();
    }

    private void initHeadView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.view_user_detail_head, null, false);

        iv_avatar = (ImageView) findHeadViewById(R.id.iv_avatar);
        tv_username = (TextView) findHeadViewById(R.id.tv_username);
        tv_follow = (TextView) findHeadViewById(R.id.tv_follow);
        tv_chat = (TextView) findHeadViewById(R.id.tv_chat);
        tv_sign = (TextView) findHeadViewById(R.id.tv_sign);
        iv_sex = (ImageView) findHeadViewById(R.id.iv_sex);
        tv_guanzhu_count = (TextView) findHeadViewById(R.id.tv_guanzhu_count);
        tv_fensi_count = (TextView) findHeadViewById(R.id.tv_fensi_count);
        tv_trend_count = (TextView) findHeadViewById(R.id.tv_trend_count);
        ll_guanzhu = (LinearLayout) findHeadViewById(R.id.ll_guanzhu);
        ll_fensi = (LinearLayout) findHeadViewById(R.id.ll_fensi);
    }

    private View findHeadViewById(int id){
        return headerView.findViewById(id);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        // title
        titleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitle("个人详情");
        titleBar.setMoreButton(0, false, null);

        // headview
        configHeadView();

        trendAdapter = new TrendAdapter(this, ArrayUtil.Array2List(detailUser.trends), rv_trend, Constant.FROM_USER_DETAIL);
        trendAdapter.addHeadView(headerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_trend.setAdapter(trendAdapter);
        rv_trend.setLayoutManager(layoutManager);
    }

    private void configHeadView() {
        imageLoader.displayImage(detailUser.userAvatar, iv_avatar, displayImageOptions);
        tv_username.setText(detailUser.userName);
        tv_sign.setText(detailUser.sign);

        tv_guanzhu_count.setText("" + detailUser.guanzhuCount);
        tv_fensi_count.setText("" + detailUser.fensiCount);
        tv_trend_count.setText("" + detailUser.trendCount);

        int height = tv_username.getLineHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_sex.getLayoutParams();
        layoutParams.height = height;
        iv_sex.setLayoutParams(layoutParams);

        if(detailUser.sex == Common.MALE){
            iv_sex.setImageDrawable(getResources().getDrawable(R.mipmap.male));
        } else {
            iv_sex.setImageDrawable(getResources().getDrawable(R.mipmap.female));
        }

//        tv_follow
        if(detailUser.isFollowed){
            tv_follow.setText("取消关注");
        } else{
            tv_follow.setText("+ 关注");
        }
        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(rv_trend, "操作成功", Snackbar.LENGTH_SHORT).show();
            }
        });
        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动会话界面
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(UserDetailActivity.this, "10010", "title显示在哪里？");
            }
        });

        ll_guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDetailActivity.this, UserListActivity.class));
            }
        });

        ll_fensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDetailActivity.this, UserListActivity.class));
            }
        });
    }

}
