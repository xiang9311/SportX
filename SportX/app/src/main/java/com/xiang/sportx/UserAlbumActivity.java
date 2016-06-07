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

import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.UserStatic;
import com.xiang.adapter.TrendAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.thread.GetTrendThread;
import com.xiang.thread.LikeTrendThread;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

public class UserAlbumActivity extends BaseAppCompatActivity {

    private MyTitleBar titleBar;
    private RecyclerView rv_album;

    // adapter
    private TrendAdapter adapter;

    // data
    private List<Common.Trend> trends = new ArrayList<>();
    private int pageIndex = 0;

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

        adapter = new TrendAdapter(this, trends, rv_album, Constant.FROM_ALBUM);
        adapter.setOnLikeItemClickListener(new TrendAdapter.OnLikeItemClickListener() {
            @Override
            public void itemLikeClick(final View view, int dataIndex, boolean isLike) {
                view.setEnabled(false);
                new LikeTrendThread(mHandler, trends.get(dataIndex).id, isLike, dataIndex).start();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                },10000);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_album.setAdapter(adapter);
        rv_album.setLayoutManager(manager);

        rv_album.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if ( ! adapter.canLoadingMore()) {
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
                        new GetTrendThread(mHandler, pageIndex, UserStatic.userId).start();
                    }
                }
            }
        });

        mHandler = new MyHandler(this, null);

        showProgress("",true);
        new GetTrendThread(mHandler, pageIndex, UserStatic.userId).start();
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
                case KEY_GET_TREND_LIST_SUC:
                    Pilot.Response10005.Data data = (Pilot.Response10005.Data) msg.obj;

                    int lastSize = trends.size();
                    trends.addAll(ArrayUtil.Array2List(data.trends));

                    if (data.maxCountPerPage > data.trends.length){
                        // 不能加载更多了
                        adapter.setCannotLoadingMore();
                    }

                    adapter.setLoadingMore(false);

                    if(lastSize == 0){
                        // 重新刷新，否则页面出来后会在底下
                        adapter.notifyDataSetChanged();
                    } else {
                        if (data.trends.length > 0) {
                            adapter.notifyItemRangeInserted(adapter.getHeadViewSize() + lastSize, data.trends.length);
                        }
                    }

                    pageIndex ++;

                    break;

                case KEY_LIKE_TREND:
                    Common.Trend trend = trends.get(msg.arg1);
                    trend.isLiked = true;
                    trend.likeCount ++;
                    adapter.notifyItemLikeChanged(msg.arg1);
                    break;
                case KEY_UNLIKE_TREND:
                    Common.Trend trend1 = trends.get(msg.arg1);
                    trend1.isLiked = false;
                    trend1.likeCount --;
                    adapter.notifyItemLikeChanged(msg.arg1);
                    break;
            }
        }
    }

}
