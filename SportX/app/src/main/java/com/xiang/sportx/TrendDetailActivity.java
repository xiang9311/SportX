package com.xiang.sportx;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiang.Util.Constant;
import com.xiang.Util.SportTimeUtil;
import com.xiang.Util.ViewUtil;
import com.xiang.adapter.TrendCommentAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;

public class TrendDetailActivity extends BaseAppCompatActivity {

    private MyTitleBar titleBar;
    private RecyclerView recyclerView;
    private View headView;

    // headview 相关内容
    private ImageView iv_avatar;
    private TextView tv_username;
    private TextView tv_content;
    private ImageView iv_big;         // 单张 大图
    private ImageView iv_images[];  // 最多9张的小图
    private TextView tv_place;
    private TextView tv_time;
    private ImageView iv_comment, iv_like;
    private TextView tv_comment_count, tv_like_count;
    private GridLayout gv_images;

    // adapter
    private TrendCommentAdapter adapter;

    //tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createAvatarIconOption();
    private DisplayImageOptions imageOptions = DisplayOptionsFactory.createNormalImageOption();

    // data
    private Common.Trend trend;
    private int from = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_trend_detail);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_comment);

        headView = LayoutInflater.from(this).inflate(R.layout.listitem_trend_follow, null ,false);
        initHeadView();
    }

    private void initHeadView() {
        iv_avatar = (ImageView) findHeadViewById(R.id.iv_follow_avatar);
        tv_username = (TextView) findHeadViewById(R.id.tv_username);
        tv_content = (TextView) findHeadViewById(R.id.tv_content);
        iv_big = (ImageView) findHeadViewById(R.id.iv_big);
        iv_images = new ImageView[]{
                (ImageView) findHeadViewById(R.id.iv_image0),
                (ImageView) findHeadViewById(R.id.iv_image1),
                (ImageView) findHeadViewById(R.id.iv_image2),
                (ImageView) findHeadViewById(R.id.iv_image3),
                (ImageView) findHeadViewById(R.id.iv_image4),
                (ImageView) findHeadViewById(R.id.iv_image5),
                (ImageView) findHeadViewById(R.id.iv_image6),
                (ImageView) findHeadViewById(R.id.iv_image7),
                (ImageView) findHeadViewById(R.id.iv_image8),
        };
        tv_place = (TextView) findHeadViewById(R.id.tv_place);
        tv_time = (TextView) findHeadViewById(R.id.tv_time);
        gv_images = (GridLayout) findHeadViewById(R.id.gv_images);

        iv_comment = (ImageView) findHeadViewById(R.id.iv_comment);
        iv_like = (ImageView) findHeadViewById(R.id.iv_like);
        tv_comment_count = (TextView) findHeadViewById(R.id.tv_comment_count);
        tv_like_count = (TextView) findHeadViewById(R.id.tv_like_count);
    }

    private View findHeadViewById(int id){
        return headView.findViewById(id);
    }

    @Override
    protected void initData() {
        from = getIntent().getIntExtra(Constant.FROM, 0);
        if(from == Constant.FROM_GYM_DETAIL){
            trend = GymDetailActivity.getLastClickTrend();
        } else if(from == Constant.FROM_FOLLOW){
            trend = MainPagerActivity.getLastClickTrend();
        }
    }

    @Override
    protected void configView() {

        titleBar.setTitle("动态详情");
        titleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setMoreButton(0, false, null);


        adapter = new TrendCommentAdapter(this, DefaultUtil.getComments(10), recyclerView);
        adapter.addHeadView(headView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        configHeadView();
    }

    private void configHeadView() {
        int count = trend.imgs.length;
        if (count > 1){
            iv_big.setVisibility(View.GONE);
            gv_images.setVisibility(View.VISIBLE);
            int i = 0;
            for(; i < count; i ++){
                iv_images[i].setVisibility(View.VISIBLE);
            }

            for(; i < iv_images.length; i ++){
                iv_images[i].setVisibility(View.GONE);
            }

        } else if (count == 1){
            iv_big.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.GONE);
        } else{
            // 隐藏图片
            iv_big.setVisibility(View.GONE);
            gv_images.setVisibility(View.GONE);
        }

        tv_username.setText(trend.briefUser.userName);
        tv_content.setText(trend.content);
        tv_place.setText(trend.gymName);
        tv_time.setText(SportTimeUtil.getDateFromNow(trend.createTime));

        imageLoader.displayImage(trend.briefUser.userAvatar, iv_avatar, options);

        if (trend.imgs.length > 1){
            int i = 0;
            for(; i < trend.imgs.length; i ++){
                imageLoader.displayImage(trend.imgs[i], iv_images[i], imageOptions);
            }

        } else if (trend.imgs.length == 1){
            imageLoader.loadImage(trend.imgs[0], new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    ViewUtil.setViewHeightByWidth(TrendDetailActivity.this, bitmap, iv_big, getResources().getDimension(R.dimen.follow_big_image_max_size));
                    iv_big.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        tv_like_count.setText(trend.likeCount + "");
        tv_comment_count.setText(trend.commentCount + "");

        if(trend.isLiked){
            iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.liked));
        } else{
            iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
        }

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(trend.isLiked){
                    trend.likeCount = trend.likeCount - 1;
                } else{
                    trend.likeCount = trend.likeCount + 1;
                }
                trend.isLiked = !trend.isLiked;

                if(trend.isLiked){
                    iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.liked));
                } else{
                    iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
                }
                tv_like_count.setText(trend.likeCount + "");
            }
        });

    }
}
