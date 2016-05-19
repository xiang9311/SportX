package com.xiang.sportx;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.model.ChoosedGym;
import com.xiang.proto.nano.Common;
import com.xiang.transport.TrendStatic;
import com.xiang.view.MyTitleBar;

import java.util.List;

public class TrendDetailActivity extends BaseAppCompatActivity {

    private static final int CODE_CHOOSE_GYM = 1;

    private MyTitleBar titleBar;
    private RecyclerView recyclerView;
    private View headView;

    // headview 相关内容
    private ImageView iv_avatar;
    private TextView tv_username, tv_gym_name, tv_send;
    private TextView tv_content;
    private ImageView iv_big;         // 单张 大图
    private ImageView iv_images[];  // 最多9张的小图
    private TextView tv_place;
    private TextView tv_time;
    private ImageView iv_comment, iv_like;
    private TextView tv_comment_count, tv_like_count;
    private GridLayout gv_images;
    private RelativeLayout rl_choose_gym, rl_comment_panel;
    private EditText et_comment_text;

    // adapter
    private TrendCommentAdapter adapter;

    //tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createAvatarIconOption();
    private DisplayImageOptions imageOptions = DisplayOptionsFactory.createNormalImageOption();

    // data
    private Common.Trend trend;
    private List<Common.Comment> comments = DefaultUtil.getComments(10);
    private boolean commentToTrend = true;   // false的话则是回复评论
    private Common.BriefUser toBriefUser;
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
        rl_choose_gym = (RelativeLayout) findViewById(R.id.rl_choose_gym);
        rl_comment_panel = (RelativeLayout) findViewById(R.id.rl_comment_panel);
        tv_gym_name = (TextView) findViewById(R.id.tv_gym_name);
        tv_send = (TextView) findViewById(R.id.tv_send);
        et_comment_text = (EditText) findViewById(R.id.et_comment_text);

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
        if(from == Constant.FROM_GYM_DETAIL || from == Constant.FROM_FOLLOW || from == Constant.FROM_USER_DETAIL
                || from == Constant.FROM_ALBUM){
            trend = TrendStatic.getLastTrend();
        } else if(from == Constant.FROM_COMMENT_MESSAGE){
            trend = DefaultUtil.getTrends(1).get(0);
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

        rl_choose_gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrendDetailActivity.this, ChooseGymActivity.class);
                startActivityForResult(intent, CODE_CHOOSE_GYM);
            }
        });

        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO comment
            }
        });



        adapter = new TrendCommentAdapter(this, comments, recyclerView);
        adapter.addHeadView(headView);
        ((RelativeLayout) headView.findViewById(R.id.rl_parent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentPanel(true, null);
            }
        });
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showCommentPanel(false, comments.get(position).briefUser);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    if(rl_comment_panel.getVisibility() != View.GONE){
                        Animation animation = AnimationUtils.loadAnimation(TrendDetailActivity.this, R.anim.jump_out);
                        rl_comment_panel.clearAnimation();
                        rl_comment_panel.startAnimation(animation);
                        rl_comment_panel.setVisibility(View.GONE);
                    }
                }
            }
        });

        configHeadView();
    }

    private void showCommentPanel(boolean isCommentToTrend, Common.BriefUser briefUser) {

        if (rl_comment_panel.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.jump_in);

            rl_comment_panel.clearAnimation();
            rl_comment_panel.setVisibility(View.VISIBLE);
            rl_comment_panel.startAnimation(animation);
        }

        this.commentToTrend = isCommentToTrend;
        this.toBriefUser = briefUser;
        if(isCommentToTrend){
            et_comment_text.setHint("评论");
        } else{
            et_comment_text.setHint("回复 " + briefUser.userName);
        }
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
                final int index = i;
                iv_images[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TrendDetailActivity.this, ImageAndTextActivity.class);
                        intent.putExtra(Constant.IMAGES, trend.imgs);
                        intent.putExtra(Constant.CURRENT_INDEX, index);
                        startActivity(intent);
                    }
                });
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
            iv_big.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TrendDetailActivity.this, ImageAndTextActivity.class);
                    intent.putExtra(Constant.IMAGES, trend.imgs);
                    startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CODE_CHOOSE_GYM:
                    ChoosedGym choosedGym = (ChoosedGym) data.getSerializableExtra(ChooseGymActivity.CHOOSED_GYM);
                    tv_gym_name.setText(choosedGym.getGymName());
                    break;
            }
        }
    }
}
