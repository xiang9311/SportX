package com.xiang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.CardUtil;
import com.xiang.Util.Constant;
import com.xiang.adapter.GymItemAdapter;
import com.xiang.adapter.UserInGymAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.GymDetailActivity;
import com.xiang.sportx.R;

import link.fls.swipestack.SwipeStack;

/**
 * Created by 祥祥 on 2016/4/26.
 */
public class GymFragment extends Fragment {

    private View mView;
    private RecyclerView rv_user_in_gym, rv_gyms;
    private ImageView iv_cover, iv_avatar;
    private TextView tv_gym_name, tv_place;
    private TextView tv_equipment_more, tv_class_more, tv_card_more;
    private TextView tv_recommend_content;
    private FloatingActionButton fab_checkin;

    private SwipeStack swipeStack_user;


    // view
    private View headerView;

    // adapter
    private UserInGymAdapter adapter;
    private GymItemAdapter gymItemAdapter;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    // data
    private Common.DetailGym detailGym = DefaultUtil.getDetailGym();

    // sp
    private SharedPreferences sp;

    private long lastCheckInTime = 0;

    // handler
    private MyHandler mHandler;

    // thread
    private CheckThread checkThread;
    private boolean isUpdate = true;

    protected void onInitFragment() {
        sp = getContext().getSharedPreferences(Constant.SP_DATA, Context.MODE_PRIVATE);
        mHandler = new MyHandler(getContext(), null);

        rv_gyms = (RecyclerView) findViewById(R.id.rv_gyms);

        rv_user_in_gym = (RecyclerView) findHeadViewById(R.id.rv_user_in_gym);
        iv_cover = (ImageView) findHeadViewById(R.id.iv_cover);
        iv_avatar = (ImageView) findHeadViewById(R.id.iv_gym_avatar);
        tv_gym_name = (TextView) findHeadViewById(R.id.tv_gym_name);
        tv_place = (TextView) findHeadViewById(R.id.tv_place);

        tv_equipment_more = (TextView) findHeadViewById(R.id.tv_equipment_more);
        tv_class_more = (TextView) findHeadViewById(R.id.tv_course_more);
        tv_card_more = (TextView) findHeadViewById(R.id.tv_card_more);
        fab_checkin = (FloatingActionButton) findHeadViewById(R.id.fab_checkin);



        initRecyclerView();

        initGymDetail();

        initGymList();

    }

    /**
     * headview find view by id
     * @param id
     * @return
     */
    private View findHeadViewById(int id){
        return headerView.findViewById(id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onInitFragment();
    }

    private void initGymList() {
        gymItemAdapter = new GymItemAdapter(getContext(), DefaultUtil.getDetailGyms(10), rv_gyms);

        // add head view
        gymItemAdapter.addHeadView(headerView);

        //TODO add foot view

        gymItemAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(getContext(), GymDetailActivity.class));
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv_gyms.setAdapter(gymItemAdapter);
        rv_gyms.setLayoutManager(manager);
    }

    /**
     * 初始化场馆详情信息
     */
    private void initGymDetail() {
        imageLoader.displayImage(detailGym.briefGym.gymCover[0], iv_cover, options);
        imageLoader.displayImage(detailGym.briefGym.gymAvatar, iv_avatar, options);
        tv_gym_name.setText(detailGym.briefGym.gymName);
        tv_place.setText(detailGym.briefGym.place);

        // 设置器材内容
        StringBuilder sb_equip = new StringBuilder();
        for(int i = 0; i < detailGym.equipments.length; i ++){
            sb_equip.append(detailGym.equipments[i].name);
            sb_equip.append(" ");
        }
        tv_equipment_more.setText(sb_equip.toString());

        // 设置课程内容
        StringBuilder sb_course = new StringBuilder();
        for(int i = 0; i < detailGym.courses.length; i ++){
            sb_course.append(detailGym.courses[i].name);
            sb_course.append(" ");
        }
        tv_class_more.setText(sb_course.toString());

        // 设置卡片内容
        StringBuilder sb_card = new StringBuilder();
        for(int i = 0; i < detailGym.gymCards.length; i ++){
            sb_card.append(CardUtil.getCardName(detailGym.gymCards[i].cardType));
            sb_card.append("/");
            sb_card.append((int)detailGym.gymCards[i].price);
            sb_card.append("元 ");
        }
        tv_card_more.setText(sb_card);


        // 打卡点击事件的一些信息
        initRl_checkin();
    }

    private void initRl_checkin() {
        // 如果上次点击了，现在计时，并设置点击事件
        final boolean checked = sp.getBoolean(Constant.CKECKED, false);
        if(checked){
            // 获取时间
            lastCheckInTime = sp.getLong(Constant.LAST_CHECKED_TIME, 0);
            // 计时线程
            isUpdate = true;
            checkThread = new CheckThread();
            checkThread.start();
        }


        // 如果没有点击，设置点击事件，并存入后重新点去init checkin
    }

    private void initRecyclerView() {
        adapter = new UserInGymAdapter(getContext(), DefaultUtil.getBriefUsers(8), rv_user_in_gym);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);


        rv_user_in_gym.setAdapter(adapter);
        rv_user_in_gym.setLayoutManager(manager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState);
        headerView = inflater.inflate(R.layout.view_gyms_header, null, false);
        mView = inflater.inflate(R.layout.view_gym, container, false);
        return mView;
    }

    private View findViewById(int id){
        return mView.findViewById(id);
    }


    private static final int KEY_UPDATE_TIME = 998;
    private static final int KEY_CHECK_OUT = 999;

    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_UPDATE_TIME:
                    long minu = System.currentTimeMillis() - lastCheckInTime;
                    break;
                case KEY_CHECK_OUT:
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constant.CKECKED, false);
                    editor.commit();

                    if (checkThread != null && checkThread.isAlive()){
                        isUpdate = false;
                    }
                    sendToast("锻炼时长：" + getTime(System.currentTimeMillis() - lastCheckInTime));
                    break;
                default:
                    break;
            }
        }
    }

    class CheckThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){
                try {
                    if( !isUpdate) {
                        break;
                    }
                    mHandler.sendEmptyMessage(KEY_UPDATE_TIME);
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private String getTime(long minu){
        long m = minu / 1000;
        long hour = m / 3600;
        long minute = (m % 3600) / 60;
        long second = m % 60;
        StringBuilder sb = new StringBuilder();
        if(hour > 0){
            sb.append(hour);
            sb.append(":");
        }
        if(minute > 9){
            sb.append(minute);
            sb.append(":");
        } else {
            sb.append("0");
            sb.append(minute);
            sb.append(":");
        }

        if (second > 9){
            sb.append(second);
        } else {
            sb.append(0);
            sb.append(second);
        }
        return sb.toString();
    }
}
