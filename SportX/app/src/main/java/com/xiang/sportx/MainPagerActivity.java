package com.xiang.sportx;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.xiang.adapter.MainPagerAdapter;
import com.xiang.fragment.FollowFragment;
import com.xiang.fragment.GymFragment;
import com.xiang.fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPagerActivity extends BaseAppCompatActivity {

    private final int SectionFragmentCOUNT = 3;

    private ViewPager viewPager;
    private RadioButton rb_discover, rb_follow, rb_user, rb[];
    private ImageView iv_search, iv_add_trend;

    private MainPagerAdapter mainPagerAdapter;
    private static List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main_pager);

        viewPager = (ViewPager) findViewById(R.id.vp_main);
        rb_discover = (RadioButton) findViewById(R.id.radio0);
        rb_follow = (RadioButton) findViewById(R.id.radio1);
//        rb_message = (RadioButton) findViewById(R.id.radio2);
        rb_user = (RadioButton) findViewById(R.id.radio2);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_add_trend = (ImageView) findViewById(R.id.iv_add_trend);
    }

    @Override
    protected void initData() {
        fragmentList = new ArrayList<>();

        GymFragment gymFragment = new GymFragment();
        Bundle gymBundle = new Bundle();
        gymBundle.putInt("type", 0);
        gymFragment.setArguments(gymBundle);
        fragmentList.add(gymFragment);

        FollowFragment followFragment = new FollowFragment();
        Bundle followBundle = new Bundle();
        followBundle.putInt("type", 0);
        followFragment.setArguments(followBundle);
        fragmentList.add(followFragment);

//        MessageFragment messageFragment = new MessageFragment();
//        Bundle messageBundler = new Bundle();
//        messageBundler.putInt("type", 0);
//        messageFragment.setArguments(messageBundler);
//        fragmentList.add(messageFragment);

        UserFragment userFragment = new UserFragment();
        Bundle userBundle = new Bundle();
        userBundle.putInt("type", 0);
        userFragment.setArguments(userBundle);
        fragmentList.add(userFragment);

        Log.d("on fragment init","on fragment 全部初始化");

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentList);
    }

    @Override
    protected void configView() {
        viewPager.setOffscreenPageLimit(4);        // 缓存的page数量
        viewPager.setAdapter(mainPagerAdapter);

        // drawables
        int size = (int) getResources().getDimension(R.dimen.bottom_image_size);
        Rect rect = new Rect(0,0, size, size);
        Drawable drawable_campass = getResources().getDrawable(R.mipmap.compass);
        drawable_campass.setBounds(rect);
        Drawable drawable_feed = getResources().getDrawable(R.mipmap.feed);
        drawable_feed.setBounds(rect);
//        Drawable drawable_message = getResources().getDrawable(R.mipmap.message);
//        drawable_message.setBounds(rect);
        Drawable drawable_user = getResources().getDrawable(R.mipmap.user);
        drawable_user.setBounds(rect);

        rb_discover.setCompoundDrawables(null, drawable_campass, null, null);
        rb_follow.setCompoundDrawables(null, drawable_feed, null, null);
//        rb_message.setCompoundDrawables(null, drawable_message, null, null);
        rb_user.setCompoundDrawables(null, drawable_user, null, null);


        rb = new RadioButton[SectionFragmentCOUNT];
        rb[0] = rb_discover;
        rb[1] = rb_follow;
        rb[2] = rb_user;

        rb[1].setAlpha(0.4f);
        rb[2].setAlpha(0.4f);

        rb_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(0);
                viewPager.setCurrentItem(0, false);
            }
        });
        rb_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(1);
                viewPager.setCurrentItem(1, false);
            }
        });
        rb_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(2);
                viewPager.setCurrentItem(2, false);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrollStateChanged(int arg0) {
                //arg = 0表示什么都没做  1表示正在滑动  2表示滑动完毕
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
//				arg0 :当前页面，及你点击滑动的页面
//				arg1:当前页面偏移的百分比
//				arg2:当前页面偏移的像素位置
//                if(arg1 > 0.1 && arg1 < 0.9)
//                    fl_wrapper.setDragable(false);

                arg1 = arg1 < 0? 0 : arg1;
                arg1 = arg1 > 1? 1 : arg1;
                int current = viewPager.getCurrentItem();    //当前会变化
                int next = arg0;
                if(current == next && current < SectionFragmentCOUNT - 1){
                    rb[current+1].setAlpha(arg1*0.6f+0.4f);
                    rb[current].setAlpha((1-arg1)*0.6f+0.4f);
                }
                else if(current > next){
                    rb[current].setAlpha(arg1*0.6f+0.4f);
                    rb[next].setAlpha((1-arg1)*0.6f+0.4f);
                }
                else{
//					Log.d(TAG,"不能往右划了");
                }
            }

            @Override
            public void onPageSelected(int arg0) {
                Log.d("onPageSelected",""+arg0);
            }

        });


        // imageview actions
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO search
            }
        });

        iv_add_trend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPagerActivity.this, CreateTrendActivity.class));
            }
        });
    }

    private void resetRbs(int index){
        rb[0].setAlpha(0.4f);
        rb[1].setAlpha(0.4f);
        rb[2].setAlpha(0.4f);
        rb[index].setAlpha(1.0f);
    }
}
