package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.CardUtil;
import com.xiang.adapter.GymImageAdapter;
import com.xiang.adapter.TrendAdapter;
import com.xiang.adapter.UserInGymAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;

public class GymDetailActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RecyclerView rv_trend_in_gym, rv_gym_images, rv_user_in_gym;

    private View headView;
    private TextView tv_gym_name, tv_place;
    private TextView tv_equipment_more, tv_class_more, tv_card_more;
    private ImageView iv_avatar;

    // adapter
    private GymImageAdapter gymImageAdapter;
    private TrendAdapter trendAdapter;
    private UserInGymAdapter userAdapter;

    // data
    private Common.DetailGym detailGym = DefaultUtil.getDetailGym();


    // tools
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_gym_detail);

        myTitleBar = (MyTitleBar) findViewById(R.id.myTitleBar);
        rv_trend_in_gym = (RecyclerView) findViewById(R.id.rv_trend_in_gym);

        headView = LayoutInflater.from(this).inflate(R.layout.view_detail_gym_header, null, false);
        // headview
        rv_gym_images = (RecyclerView) findHeadViewById(R.id.rv_image_in_gym);
        rv_user_in_gym = (RecyclerView) findHeadViewById(R.id.rv_user_in_gym);
        iv_avatar = (ImageView) findHeadViewById(R.id.iv_gym_avatar);
        tv_gym_name = (TextView) findHeadViewById(R.id.tv_gym_name);
        tv_place = (TextView) findHeadViewById(R.id.tv_place);
        tv_equipment_more = (TextView) findHeadViewById(R.id.tv_equipment_more);
        tv_class_more = (TextView) findHeadViewById(R.id.tv_course_more);
        tv_card_more = (TextView) findHeadViewById(R.id.tv_card_more);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myTitleBar.setTitle("某个健身房");
        myTitleBar.setMoreButton(0, false, null);

        initDetailGym();

        initImageRecyclerView();

        initTrendRecyclerView();

        initUserRecyclerView();
    }

    private void initUserRecyclerView() {
        userAdapter = new UserInGymAdapter(this, DefaultUtil.getBriefUsers(8), rv_user_in_gym);
        userAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        rv_user_in_gym.setAdapter(userAdapter);
        rv_user_in_gym.setLayoutManager(manager);
    }

    private void initTrendRecyclerView() {
        trendAdapter = new TrendAdapter(this, DefaultUtil.getTrends(10), rv_trend_in_gym);

        // add head view
        trendAdapter.addHeadView(headView);

        //TODO add foot view

        trendAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(GymDetailActivity.this, GymDetailActivity.class));
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_trend_in_gym.setAdapter(trendAdapter);
        rv_trend_in_gym.setLayoutManager(manager);
    }

    private void initImageRecyclerView() {
        gymImageAdapter = new GymImageAdapter(this, ArrayUtil.Array2List(detailGym.briefGym.gymCover), rv_gym_images);
        gymImageAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rv_gym_images.setAdapter(gymImageAdapter);
        rv_gym_images.setLayoutManager(manager);
    }

    private void initDetailGym() {
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
    }

    private View findHeadViewById(int id){
        return headView.findViewById(id);
    }
}
