package com.xiang.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.Constant;
import com.xiang.Util.ViewUtil;
import com.xiang.adapter.TrendAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

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

    private View headView;
    private TextView tv_messageCount;
    private ImageView iv_message_avatar;
    private RelativeLayout rl_messageCount_parent;

    // data
    private List<Common.Trend> trendList = DefaultUtil.getTrends(20);
    Common.TrendBriefMessage message = DefaultUtil.getTrendBriefMessage();

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
            // TODO trend 没有内容 则需要显示推荐的列表
        }
    }

    private void initHead() {

        ViewGroup.LayoutParams layoutParams = headView.getLayoutParams();
        if(null == layoutParams) {
            layoutParams = new ViewGroup.LayoutParams(ViewUtil.getWindowWidth(getContext()), (int) getContext().getResources().getDimension(R.dimen.new_message_height));
        }
        layoutParams.width = ViewUtil.getWindowWidth(getContext());
        headView.setLayoutParams(layoutParams);

        TextView tv_count = (TextView) headView.findViewById(R.id.tv_message);
        RelativeLayout rl_parent = (RelativeLayout) headView.findViewById(R.id.rl_parent);
        ImageView iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);

        tv_count.setText(message.count + "条新消息");
        imageLoader.displayImage(message.lastAvatar, iv_avatar, avatarOptions);
        rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trendAdapter.removeFootView();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("onActivityCreated", "onActivityCreated");
    }

    private boolean initTrend() {
        if (trendList.size() > 0){

            trendAdapter = new TrendAdapter(getContext(), trendList, rv_trend, Constant.FROM_FOLLOW);

            if(message.count > 0){
                trendAdapter.addHeadView(headView);
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            rv_trend.setAdapter(trendAdapter);
            rv_trend.setLayoutManager(layoutManager);

            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d("onCreateView", "onCreateView");
        mView = inflater.inflate(R.layout.view_follow, container, false);

        headView = inflater.inflate(R.layout.view_new_message, null, false);

        return mView;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                if(!trendAdapter.hasHead()){
                    trendAdapter.addHeadView(headView);
                }
            }
        }, 3000);
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
    }

}
