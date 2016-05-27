package com.xiang.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ViewUtil;
import com.xiang.adapter.BannerAdapter;
import com.xiang.adapter.PreferRecyclerAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by 祥祥 on 2016/4/21.
 */
public class DiscoverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private View mView;
    private ViewPager viewPager;                           // banner viewpager
    private RecyclerView rv_prefer_trend, rv_feed;
    private BannerAdapter bannerAdapter;
    private CircleIndicator indicator;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<View> bannerViews;

    // adapter
    private PreferRecyclerAdapter preferRecyclerAdapter, feedAdapter;

    // tools
    private DisplayImageOptions displayImageOptions = DisplayOptionsFactory.createNormalImageOption();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("onActivityCreated","onActivityCreated");

        viewPager = (ViewPager) mView.findViewById(R.id.vp_banner);
        rv_prefer_trend = (RecyclerView) mView.findViewById(R.id.rv_prefer);
        rv_feed = (RecyclerView) mView.findViewById(R.id.rv_feed);
        indicator = (CircleIndicator) mView.findViewById(R.id.indicator);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.refreshLayout);

        // init bannerView
        initBanner();

        // init prefer
        initPrefer();
        
        // init gyms
        initGyms();
        
        // init users
        initUsers();

        // init 推荐内容
        initRecommend();

        swipeRefreshLayout.setOnRefreshListener(this);

        viewPager.requestFocus();
    }

    private void initRecommend() {
        List<Common.Trend> trends = DefaultUtil.getTrends(10);
        feedAdapter = new PreferRecyclerAdapter(getContext(), trends, rv_feed);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        // Create a drawable for your divider
        Drawable drawable = getResources().getDrawable(R.drawable.white_divider);
        // Create a DividerItemDecoration instance with a single layer and add it to your recycler view
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(new Layer(DividerBuilder.get().with(drawable).build()));
//        rv_feed.addItemDecoration(itemDecoration);
        rv_feed.setAdapter(feedAdapter);
        rv_feed.setLayoutManager(gridLayoutManager);
    }

    private void initUsers() {
        List<Common.BriefUser> briefUsers = DefaultUtil.getBriefUsers(5);
        int[] avatarId = new int[]{R.id.civ_user_01, R.id.civ_user_02, R.id.civ_user_03, R.id.civ_user_04, R.id.civ_user_05};
        int[] userName = new int[]{
                R.id.tv_username_01,
                R.id.tv_username_02,
                R.id.tv_username_03,
                R.id.tv_username_04,
                R.id.tv_username_05
        };
        try{
            float margin = getResources().getDimension(R.dimen.prefer_image_margin);
            float padding = getResources().getDimension(R.dimen.padding_lr);
            float space = margin * 4 + padding * 2;
            int size = (int) ((ViewUtil.getWindowWidth(getContext()) - space) / 5);

            for(int i = 0 ; i < briefUsers.size(); i ++){
                ImageView imageView = (ImageView) mView.findViewById(avatarId[i]);

                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = size;
                layoutParams.height = size;
                imageView.setLayoutParams(layoutParams);

                TextView tv_name = (TextView) mView.findViewById(userName[i]);

                imageLoader.displayImage(briefUsers.get(i).userAvatar, imageView, displayImageOptions);
                tv_name.setText(briefUsers.get(i).userName);
            }
        } catch (IndexOutOfBoundsException e){

        }
    }

    private void initGyms() {
        int[] imageViewId = new int[]{R.id.iv_gym0, R.id.iv_gym1, R.id.iv_gym2};
        int[] textViewId = new int[]{R.id.tv_gym0, R.id.tv_gym1, R.id.tv_gym2};
        int[] placeViewId = new int[]{R.id.tv_place0, R.id.tv_place1, R.id.tv_place2};
        try{
            List<Common.BriefGym> gyms = DefaultUtil.getGyms(3);
            for(int i = 0 ; i < gyms.size(); i ++){
                ImageView imageView = (ImageView) mView.findViewById(imageViewId[i]);
                TextView tv_name = (TextView) mView.findViewById(textViewId[i]);
                TextView tv_place = (TextView) mView.findViewById(placeViewId[i]);

                imageLoader.displayImage(gyms.get(i).gymCover[0], imageView, displayImageOptions);
                tv_name.setText(gyms.get(i).gymName);
                tv_place.setText(gyms.get(i).place);
            }
        } catch (IndexOutOfBoundsException e){

        }
    }

    private void initPrefer() {
        List<Common.Trend> trends = DefaultUtil.getTrends(4);
        preferRecyclerAdapter = new PreferRecyclerAdapter(getContext(), trends, rv_prefer_trend);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        // Create a drawable for your divider
        Drawable drawable = getResources().getDrawable(R.drawable.white_divider);

        // Create a DividerItemDecoration instance with a single layer and add it to your recycler view
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(new Layer(DividerBuilder.get().with(drawable).build()));

//        rv_prefer_trend.addItemDecoration(itemDecoration);

        rv_prefer_trend.setAdapter(preferRecyclerAdapter);
        rv_prefer_trend.setLayoutManager(gridLayoutManager);

    }

    private void initBanner() {
        // viewpager 宽/高 = 0.35
        int width = ViewUtil.getWindowWidth(getContext());
        int height = (int) (width * 0.35);
        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = height;
        viewPager.setLayoutParams(layoutParams);

        List<Common.Banner> banners = DefaultUtil.getBanner(4);
        bannerViews = new ArrayList<>();
        for(int i = 0; i < banners.size(); i ++){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_banneritem, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_banneritem);
            imageLoader.displayImage(banners.get(i).coverUrl, imageView, displayImageOptions);
            bannerViews.add(view);
        }
        bannerAdapter = new BannerAdapter(getContext(), bannerViews, viewPager);
//        viewPager.setPageTransformer(false, new ParallaxPagerTransformer(R.id.iv_banneritem));
        viewPager.setAdapter(bannerAdapter);
        indicator.setViewPager(viewPager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView","onCreateView");
        mView = inflater.inflate(R.layout.view_discover, container, false);
        return mView;
    }

    @Override
    public void onRefresh() {
        new BaseHandler(getContext(), null).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onStart() {
        Log.d("onStart","onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("onResume","onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("onPause","onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("onStop","onStop");
        super.onStop();
    }
}
