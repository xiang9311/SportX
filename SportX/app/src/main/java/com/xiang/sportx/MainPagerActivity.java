package com.xiang.sportx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiang.Util.ActivityUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.UserStatic;
import com.xiang.adapter.MainPagerAdapter;
import com.xiang.database.helper.BriefUserHelper;
import com.xiang.database.model.TblBriefUser;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.fragment.FollowFragment;
import com.xiang.fragment.GymFragment;
import com.xiang.fragment.MessageFragment;
import com.xiang.fragment.UserFragment;
import com.xiang.view.TwoOptionMaterialDialog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class MainPagerActivity extends BaseAppCompatActivity {

    private final int SectionFragmentCOUNT = 4;

    private ViewPager viewPager;
    private RelativeLayout rl_discover, rl_follow, rl_user, rl[], rl_chat;
    private ImageView iv_search, iv_add_trend;
    private TwoOptionMaterialDialog md_login_register;

    private MainPagerAdapter mainPagerAdapter;
    private static List<Fragment> fragmentList;

    private BriefUserHelper briefUserHelper = new BriefUserHelper(this);
    private SharedPreferences sp;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.BROADCAST_NEW_COMMENT_MESSAGE.equals(intent.getAction())){
                try{
                    if (viewPager.getCurrentItem() == 1 && ActivityUtil.isForeground(MainPagerActivity.this)){ // 关注在1的位置
                        ((FollowFragment) fragmentList.get(1)).notifyNewComment();
                    } else{
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(Constant.SP_NEW_COMMENT_MESSAGE, true);
                        editor.commit();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if(Constant.BROADCAST_GOTO_MESSAGE_ACTIVITY.equals(intent.getAction())){
                startActivity(new Intent(MainPagerActivity.this, CommentMessageActivity.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置融云信息提供者
        setUserInfoProvider();
    }

    private void setUserInfoProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                TblBriefUser tblBriefUser = briefUserHelper.getUserById(s);
                if(tblBriefUser != null) {
                    Uri uri = Uri.parse(tblBriefUser.userAvatar);
                    return new UserInfo(tblBriefUser.id, tblBriefUser.userName, uri);
                } else{
                    return null;
                }
            }
        }, true);
    }


    @Override
    protected void initView() {
        sp = getSharedPreferences(Constant.SP_DATA, MODE_PRIVATE);

        setContentView(R.layout.activity_main_pager);

        viewPager = (ViewPager) findViewById(R.id.vp_main);
        rl_discover = (RelativeLayout) findViewById(R.id.rl_discover);
        rl_follow = (RelativeLayout) findViewById(R.id.rl_follow);
        rl_chat = (RelativeLayout) findViewById(R.id.rl_chat);
        rl_user = (RelativeLayout) findViewById(R.id.rl_user);
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

        MessageFragment messageFragment = new MessageFragment();
        Bundle messageBundler = new Bundle();
        messageBundler.putInt("type", 0);
        messageFragment.setArguments(messageBundler);
        fragmentList.add(messageFragment);

        UserFragment userFragment = new UserFragment();
        Bundle userBundle = new Bundle();
        userBundle.putInt("type", 0);
        userFragment.setArguments(userBundle);
        fragmentList.add(userFragment);

        Log.d("on fragment init", "on fragment 全部初始化");

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentList);
    }

    @Override
    protected void configView() {
        viewPager.setOffscreenPageLimit(4);        // 缓存的page数量
        viewPager.setAdapter(mainPagerAdapter);

        rl = new RelativeLayout[SectionFragmentCOUNT];
        rl[0] = rl_discover;
        rl[1] = rl_follow;
        rl[2] = rl_chat;
        rl[3] = rl_user;

        rl[1].setAlpha(0.4f);
        rl[2].setAlpha(0.4f);
        rl[3].setAlpha(0.4f);

        rl_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(0);
                viewPager.setCurrentItem(0, false);
            }
        });
        rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(1);
                viewPager.setCurrentItem(1, false);
            }
        });
        rl_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(2);
                viewPager.setCurrentItem(2, false);
            }
        });
        rl_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetRbs(3);
                viewPager.setCurrentItem(3, false);
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
                    rl[current+1].setAlpha(arg1*0.6f+0.4f);
                    rl[current].setAlpha((1-arg1)*0.6f+0.4f);
                }
                else if(current > next){
                    rl[current].setAlpha(arg1*0.6f+0.4f);
                    rl[next].setAlpha((1-arg1)*0.6f+0.4f);
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
                startActivity(new Intent(MainPagerActivity.this, SearchActivity.class));
            }
        });

        iv_add_trend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserStatic.logged) {
                    startActivity(new Intent(MainPagerActivity.this, CreateTrendActivity.class));
                } else{
                    if(md_login_register == null){
                        String[] options = new String[]{"登录", "注册"};
                        md_login_register = MaterialDialogFactory.createTwoOptionMd(MainPagerActivity.this, options, false, 0, true);
                        md_login_register.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                            @Override
                            public void onOptionChoose(int index) {
                                if(index == 0){
                                    startActivity(new Intent(MainPagerActivity.this, LoginActivity.class));
                                } else{
                                    startActivity(new Intent(MainPagerActivity.this, RegisterActivity.class));
                                }
                            }
                        });
                        md_login_register.setTitle("您还未登录");
                        md_login_register.setCanceledOnTouchOutside(true);
                    }
                    md_login_register.show();
                }
            }
        });

        // 注册新消息广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_NEW_COMMENT_MESSAGE);
        intentFilter.addAction(Constant.BROADCAST_GOTO_MESSAGE_ACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    private void resetRbs(int index){
        rl[0].setAlpha(0.4f);
        rl[1].setAlpha(0.4f);
        rl[2].setAlpha(0.4f);
        rl[3].setAlpha(0.4f);
        rl[index].setAlpha(1.0f);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
