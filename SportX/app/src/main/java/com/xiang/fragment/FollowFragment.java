package com.xiang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.adapter.TrendAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.proto.trend.nano.Trend;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.sportx.CommentMessageActivity;
import com.xiang.sportx.R;
import com.xiang.thread.LikeTrendThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 祥祥 on 2016/4/25.
 */
public class FollowFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View mView;
    private RecyclerView rv_trend;
    private RecyclerView rv_user;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TrendAdapter trendAdapter;

    private TextView tv_messageCount;
    private ImageView iv_message_avatar;
    private RelativeLayout rl_messageCount_parent;
    private LinearLayout ll_parent;

    // data
    private List<Common.Trend> trendList = new ArrayList<>();
    Common.TrendBriefMessage message = DefaultUtil.getTrendBriefMessage();
    private int trendPageIndex = 0;
    private boolean addMoreTrend = false;
    private int maxCountPerPage_trend = 0;

    private boolean canLoadingMoreTrend = false;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions avatarOptions = DisplayOptionsFactory.createAvatarIconOption();

    @Override
    protected void onInitFragment() {
        rv_trend = (RecyclerView) mView.findViewById(R.id.rv_follow_trend);
        rv_user = (RecyclerView) mView.findViewById(R.id.rv_recommend_user);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.srl_follow);

        swipeRefreshLayout.setOnRefreshListener(this);

        initHead();
        // init trend
        if ( ! initTrend()){
            // TODO trend 没有内容 则需要显示推荐的列表, 此操作应该在获取trend之后进行
        }

        mHandler = new MyHandler(getContext(), swipeRefreshLayout);
        new GetFollowTrendThread().start();

        //TODO 未登录的话获取推荐的trend
    }

    private void initHead() {

        tv_messageCount = (TextView) mView.findViewById(R.id.tv_message);
        rl_messageCount_parent = (RelativeLayout) mView.findViewById(R.id.rl_parent);
        iv_message_avatar = (ImageView) mView.findViewById(R.id.iv_avatar);
        ll_parent = (LinearLayout) mView.findViewById(R.id.ll_parent);

        rl_messageCount_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CommentMessageActivity.class));
                rl_messageCount_parent.setVisibility(View.GONE);
            }
        });

    }

    private void configHead(){
        tv_messageCount.setText(message.count + "条新消息");
        imageLoader.displayImage(message.lastAvatar, iv_message_avatar, avatarOptions);
        if(message.count > 0){
            // 弹出动画
            // 3秒后自动消失
            Animation topinAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.top_in);
            Animation fadeinAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            rl_messageCount_parent.setVisibility(View.VISIBLE);
            rl_messageCount_parent.startAnimation(topinAnimation);
            ll_parent.startAnimation(fadeinAnimation);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(rl_messageCount_parent.getVisibility() == View.VISIBLE){
                        Animation topoutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.top_out);
                        Animation fadeoutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

                        rl_messageCount_parent.startAnimation(topoutAnimation);
                        ll_parent.startAnimation(fadeoutAnimation);
                        rl_messageCount_parent.setVisibility(View.GONE);
                    }
                }
            }, 3000);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("onActivityCreated", "onActivityCreated");
    }

    private boolean initTrend() {
        trendAdapter = new TrendAdapter(getContext(), trendList, rv_trend, Constant.FROM_FOLLOW);
        trendAdapter.setOnLikeItemClickListener(new TrendAdapter.OnLikeItemClickListener() {
            @Override
            public void itemLikeClick(int dataIndex, boolean isLike) {
                new LikeTrendThread(mHandler, trendList.get(dataIndex).id, isLike, dataIndex).start();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv_trend.setAdapter(trendAdapter);
        rv_trend.setLayoutManager(layoutManager);
        rv_trend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!canLoadingMoreTrend) {
                    return;
                }

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (trendAdapter.isLoadingMore()) {
                        Log.d("isloadingmore", "ignore manually update!");
                    } else {
                        trendAdapter.setLoadingMore(true);
                        addMoreTrend = true;

                        new GetFollowTrendThread().start();
                    }
                }
            }
        });

        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d("onCreateView", "onCreateView");
        mView = inflater.inflate(R.layout.view_follow, container, false);


        return mView;
    }

    @Override
    public void onRefresh() {
        addMoreTrend = false;
        trendPageIndex = 0;
        new GetFollowTrendThread().start();
    }

    private final int KEY_GET_TRENDS_SUC = 101;
    private final int KEY_GET_BRIEF_MESSAGE_SUC = 102;
    private MyHandler mHandler;
    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_TRENDS_SUC:
                    Trend.Response12002.Data data = (Trend.Response12002.Data) msg.obj;

                    int lastSize = trendList.size();
                    if(addMoreTrend){
                        trendList.addAll(ArrayUtil.Array2List(data.trends));
                        trendAdapter.setLoadingMore(false);
                    } else{
                        trendList.clear();
                        trendPageIndex = 0;
                        trendList.addAll(ArrayUtil.Array2List(data.trends));
                        trendAdapter.setLoadingMore(false);
                    }
                    maxCountPerPage_trend = data.maxCountPerPage;

                    if(addMoreTrend && data.trends.length > 0){
                        trendAdapter.notifyItemRangeInserted(trendAdapter.headViews.size() + lastSize, data.trends.length);
                    } else{
                        trendAdapter.notifyDataSetChanged();
                    }

                    if(maxCountPerPage_trend > data.trends.length){
                        canLoadingMoreTrend = false;
                        trendAdapter.setCannotLoadingMore();
                    } else{
                        canLoadingMoreTrend = true;
                    }

                    trendPageIndex ++;
                    swipeRefreshLayout.setRefreshing(false);
                    break;

                case KEY_LIKE_TREND:
                    Common.Trend trend = trendList.get(msg.arg1);
                    trend.isLiked = true;
                    trend.likeCount ++;
                    trendAdapter.notifyItemLikeChanged(msg.arg1);
                    break;
                case KEY_UNLIKE_TREND:
                    Common.Trend trend1 = trendList.get(msg.arg1);
                    trend1.isLiked = false;
                    trend1.likeCount --;
                    trendAdapter.notifyItemLikeChanged(msg.arg1);
                    break;
                /**
                 * 获取简要trend消息
                 */
                case KEY_GET_BRIEF_MESSAGE_SUC:
                    message = (Common.TrendBriefMessage) msg.obj;
                    configHead();
                    break;
            }
        }
    }

    class GetFollowTrendThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10011;
            Trend.Request12002 request = new Trend.Request12002();
            Trend.Request12002.Params params = new Trend.Request12002.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.pageIndex = trendPageIndex;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_FOLLOW_TREND, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Trend.Response12002 response = Trend.Response12002.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.obj = response.data;
                            msg.what = KEY_GET_TRENDS_SUC;
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

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop", "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("onDetach", "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("onDestroyView", "onDestroyView");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            SharedPreferences sp = getContext().getSharedPreferences(Constant.SP_DATA, Context.MODE_PRIVATE);
            if (sp.getBoolean(Constant.SP_NEW_COMMENT_MESSAGE, false)){
                notifyNewComment();
            }
        }
    }

    public void notifyNewComment(){
        SharedPreferences sp = getContext().getSharedPreferences(Constant.SP_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constant.SP_NEW_COMMENT_MESSAGE, false);
        editor.commit();
        new GetTrendBriefMessage().start();
    }

    class GetTrendBriefMessage extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10017;
            Pilot.Request10017 request = new Pilot.Request10017();
            Pilot.Request10017.Params params = new Pilot.Request10017.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_TREND_BRIEF_MESSAGE, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10017 response = Pilot.Response10017.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_GET_BRIEF_MESSAGE_SUC;
                            msg.obj = response.data.trendBriefMessage;
                            if (msg.obj != null) {  // 没有消息时不用任何操作
                                mHandler.sendMessage(msg);
                            }
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
