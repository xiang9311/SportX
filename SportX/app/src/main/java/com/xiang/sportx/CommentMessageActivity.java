package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiang.Util.Constant;
import com.xiang.adapter.CommentMessageAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;

import java.util.List;

public class CommentMessageActivity extends BaseAppCompatActivity {

    private MyTitleBar titleBar;
    private RecyclerView rv_comment_message;

    // adapter
    private CommentMessageAdapter adapter;

    // data
    private List<Common.CommentMessage> messages = DefaultUtil.getCommentMessage(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_comment_message);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        rv_comment_message = (RecyclerView) findViewById(R.id.rv_comment_message);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        titleBar.setBackButtonDefault();
        titleBar.setTitle("消息");
        titleBar.setMoreTextButton("清空", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        adapter = new CommentMessageAdapter(this, messages, rv_comment_message);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CommentMessageActivity.this, TrendDetailActivity.class);
                intent.putExtra(Constant.FROM, Constant.FROM_COMMENT_MESSAGE);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_comment_message.setAdapter(adapter);
        rv_comment_message.setLayoutManager(manager);
    }
}
