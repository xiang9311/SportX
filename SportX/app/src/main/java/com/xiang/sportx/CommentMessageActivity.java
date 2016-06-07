package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.adapter.CommentMessageAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

public class CommentMessageActivity extends BaseAppCompatActivity {

    private MyTitleBar titleBar;
    private RecyclerView rv_comment_message;

    // adapter
    private CommentMessageAdapter adapter;

    // data
    private List<Common.CommentMessage> messages = new ArrayList<>();
    private int pageIndex = 0;

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
                new DeleteCommentMessageThread(true, null).start();
            }
        });

        adapter = new CommentMessageAdapter(this, messages, rv_comment_message);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CommentMessageActivity.this, TrendDetailActivity.class);
                intent.putExtra(Constant.FROM, Constant.FROM_COMMENT_MESSAGE);
                intent.putExtra(Constant.TREND_ID, messages.get(position).trendId);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_comment_message.setAdapter(adapter);
        rv_comment_message.setLayoutManager(manager);

        rv_comment_message.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!adapter.canLoadingMore()) {
                    return;
                }

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (adapter.isLoadingMore()) {
                        Log.d("isloadingmore", "ignore manually update!");
                    } else {
                        adapter.setLoadingMore(true);

                        new GetCommentMessageThread().start();
                    }
                }
            }
        });

        mHandler = new MyHandler(this, null);

        showProgress("", true);
        new GetCommentMessageThread().start();
    }

    private MyHandler mHandler;

    private final int KEY_GET_MESSAGE_SUC = 101;
    private final int KEY_DELETE_MESSAGE_SUC = 102;

    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_MESSAGE_SUC:
                    Pilot.Response10006.Data data = (Pilot.Response10006.Data) msg.obj;
                    int lastSize = messages.size();
                    messages.addAll(ArrayUtil.Array2List(data.commentMessages));
                    if(lastSize < messages.size() && data.commentMessages.length > 0) {
                        adapter.notifyItemRangeInserted(lastSize + adapter.getHeadViewSize(), data.commentMessages.length);
                    }

                    if (data.maxCountPerPage > data.commentMessages.length){
                        adapter.setCannotLoadingMore();
                    }

                    pageIndex ++;

                    break;
                case KEY_DELETE_MESSAGE_SUC:
                    messages.clear();
                    adapter.notifyDataSetChanged();
                    new GetCommentMessageThread().start();
                    break;
            }
        }
    }

    class GetCommentMessageThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10006;
            Pilot.Request10006 request = new Pilot.Request10006();
            Pilot.Request10006.Params params = new Pilot.Request10006.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.pageIndex = pageIndex;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_MY_COMMENT_MESSAGE, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10006 response = Pilot.Response10006.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_GET_MESSAGE_SUC;
                            msg.obj = response.data;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }

    class DeleteCommentMessageThread extends Thread{

        boolean clearAll = false;
        int[] messageIds;

        public DeleteCommentMessageThread (boolean clearAll, int[] messageIds){
            this.clearAll = clearAll;
            this.messageIds = messageIds;
        }

        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10007;
            Pilot.Request10007 request = new Pilot.Request10007();
            Pilot.Request10007.Params params = new Pilot.Request10007.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            if (clearAll){
                params.clearAll = true;
            } else{
                params.messageIds = messageIds;
            }

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_DELETE_MY_COMMENT_MESSAGE, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10007 response = Pilot.Response10007.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_DELETE_MESSAGE_SUC;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }
}
