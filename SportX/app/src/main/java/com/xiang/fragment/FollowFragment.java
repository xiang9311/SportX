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

import com.xiang.Util.Constant;
import com.xiang.adapter.TrendAdapter;
import com.xiang.dafault.DefaultUtil;
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

    // data
    private List<Common.Trend> trendList = DefaultUtil.getTrends(20);

    @Override
    protected void onInitFragment() {
        rv_trend = (RecyclerView) mView.findViewById(R.id.rv_follow_trend);
        rv_user = (RecyclerView) mView.findViewById(R.id.rv_recommend_user);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.srl_follow);

        swipeRefreshLayout.setOnRefreshListener(this);

        // init trend
        if ( ! initTrend()){

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("onActivityCreated", "onActivityCreated");
    }

    private boolean initTrend() {
        if (trendList.size() > 0){

            trendAdapter = new TrendAdapter(getContext(), trendList, rv_trend, Constant.FROM_FOLLOW);
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
        super.onCreateView( inflater, container, savedInstanceState);
        Log.d("onCreateView", "onCreateView");
        mView = inflater.inflate(R.layout.view_follow, container, false);
        return mView;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
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

    public Common.Trend getLastClickTrend() {
        return trendAdapter.getLastClickTrend();
    }
}
