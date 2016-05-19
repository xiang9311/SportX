package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiang.Util.Constant;
import com.xiang.adapter.TrendAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.view.MyTitleBar;

public class UserAlbumActivity extends BaseAppCompatActivity {

    private MyTitleBar titleBar;
    private RecyclerView rv_album;

    // adapter
    private TrendAdapter adapter;

    // tools

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_user_album);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        rv_album = (RecyclerView) findViewById(R.id.rv_my_album);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        titleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitle("相册");
        titleBar.setMoreButton(R.mipmap.message, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAlbumActivity.this, CommentMessageActivity.class));
            }
        });

        adapter = new TrendAdapter(this, DefaultUtil.getTrends(20), rv_album, Constant.FROM_ALBUM);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_album.setAdapter(adapter);
        rv_album.setLayoutManager(manager);
    }
}
