package com.xiang.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 祥祥 on 2016/4/21.
 */
public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<View> views;
    private ViewPager viewPager;

    public BannerAdapter(Context context, List<View> views, ViewPager viewPager) {
        this.context = context;
        this.views = views;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        position = position % views.size();
        container.addView(views.get(position), 0);
        return views.get(position);
    }
    /**
     * 适配器移除container容器中的视图
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        position = position % views.size();
        container.removeView(views.get(position));
    }

}
