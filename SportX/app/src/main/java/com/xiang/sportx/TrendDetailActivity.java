package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.SportTimeUtil;
import com.xiang.Util.SportXIntent;
import com.xiang.Util.ViewUtil;
import com.xiang.Util.WindowUtil;
import com.xiang.adapter.TrendCommentAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.model.ChoosedGym;
import com.xiang.proto.nano.Common;
import com.xiang.proto.trend.nano.Trend;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.thread.LikeTrendThread;
import com.xiang.transport.TrendStatic;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

public class TrendDetailActivity extends BaseAppCompatActivity {

    private static final int CODE_CHOOSE_GYM = 1;

    private MyTitleBar titleBar;
    private RecyclerView recyclerView;
    private View headView, headTitleView;

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
    private List<Common.Comment> comments = new ArrayList<>();
    private boolean commentToTrend = true;   // false的话则是回复评论
    private Common.BriefUser toBriefUser;
    private int toCommentId;
    private int from = 0;

    private int comment_index = 0;
    private int maxCountPerPage = 0;
    private boolean refresh = true;
    private ChoosedGym choosedGym;
    private boolean canLoadingMore = true;

    int itemViewInspactSize;
    int bigInspactSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_trend_detail);

        int windowWidth = ViewUtil.getWindowWidth(this);
        float usedWidth = getResources().getDimension(R.dimen.used_size_of_trend);
        float margin = getResources().getDimension(R.dimen.follow_image_margin);
        bigInspactSize = (int) ((windowWidth - usedWidth) * 0.9f);
        itemViewInspactSize = (int) (((windowWidth - usedWidth) * 0.9f - 2 * margin) / 3);        // 占用90%的空间。每行三个


        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_comment);
        rl_choose_gym = (RelativeLayout) findViewById(R.id.rl_choose_gym);
        rl_comment_panel = (RelativeLayout) findViewById(R.id.rl_comment_panel);
        tv_gym_name = (TextView) findViewById(R.id.tv_gym_name);
        tv_send = (TextView) findViewById(R.id.tv_send);
        et_comment_text = (EditText) findViewById(R.id.et_comment_text);

        headView = LayoutInflater.from(this).inflate(R.layout.listitem_trend_follow, null ,false);
        headTitleView = LayoutInflater.from(this).inflate(R.layout.view_comment_title, null, false);
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

        for (int i =0; i < iv_images.length; i ++){
            GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) iv_images[i].getLayoutParams();
            layoutParams.width = itemViewInspactSize;
            layoutParams.height = itemViewInspactSize;
            iv_images[i].setLayoutParams(layoutParams);
        }
        iv_big.setMaxWidth(bigInspactSize);
        iv_big.setMaxHeight(bigInspactSize);
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
            int trendId = getIntent().getIntExtra(Constant.TREND_ID, 0);
            configViewLater = true;
            new GetTrendByIdThread(trendId).start();
        }

        titleBar.setTitle("动态详情");
        titleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setMoreButton(0, false, null);

        mHandler = new MyHandler(this, null);
    }

    @Override
    protected void configView() {
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

                if (et_comment_text.getText().toString().length() > Constant.MAX_LENGTH_TREND_COMMENT){
                    sendToast("您输入的内容过长。应小于100字。");
                    return;
                }

                if (et_comment_text.getText().toString().equals("")){
                    sendToast("您还未输入内容");
                    return;
                }

                new CommentThread().start();
                Animation animation = AnimationUtils.loadAnimation(TrendDetailActivity.this, R.anim.jump_out);
                rl_comment_panel.clearAnimation();
                rl_comment_panel.startAnimation(animation);
                rl_comment_panel.setVisibility(View.GONE);
                WindowUtil.hideInputMethod(TrendDetailActivity.this);
            }
        });

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SportXIntent.gotoUserDetail(TrendDetailActivity.this, trend.briefUser.userId, trend.briefUser.userName);
            }
        });

        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SportXIntent.gotoUserDetail(TrendDetailActivity.this, trend.briefUser.userId, trend.briefUser.userName);
            }
        });

        adapter = new TrendCommentAdapter(this, comments, recyclerView);
        adapter.addHeadView(headView);
        adapter.addHeadView(headTitleView);
        headView.findViewById(R.id.rl_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentPanel(true, null, 0);
            }
        });
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showCommentPanel(false, comments.get(position).briefUser, comments.get(position).commentId);
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
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (rl_comment_panel.getVisibility() != View.GONE) {
                        Animation animation = AnimationUtils.loadAnimation(TrendDetailActivity.this, R.anim.jump_out);
                        rl_comment_panel.clearAnimation();
                        rl_comment_panel.startAnimation(animation);
                        rl_comment_panel.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!canLoadingMore) {
                    return;
                }

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (adapter.isLoadingMore()) {
                        Log.d("isloadingmore", "ignore manually update!");
                    } else {
                        adapter.setLoadingMore(true);
                        new GetCommentThread().start();
                    }
                }
            }
        });

        configHeadView();

        new GetCommentThread().start();
    }

    private void showCommentPanel(boolean isCommentToTrend, Common.BriefUser briefUser, int toCommentId) {

        if (rl_comment_panel.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.jump_in);

            rl_comment_panel.clearAnimation();
            rl_comment_panel.setVisibility(View.VISIBLE);
            rl_comment_panel.startAnimation(animation);
        }

        this.commentToTrend = isCommentToTrend;
        this.toBriefUser = briefUser;
        this.toCommentId = toCommentId;
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
        if(trend.content == null || trend.content.equals("")){
            tv_content.setVisibility(View.GONE);
        }else {
            tv_content.setText(trend.content);
        }
        if(trend.gymName == null || trend.gymName.equals("")){
            tv_place.setVisibility(View.GONE);
        } else {
            tv_place.setText(trend.gymName);
        }
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
                    ViewUtil.setViewSizeByUsedWidth(TrendDetailActivity.this, bitmap, iv_big,
                            getResources().getDimension(R.dimen.used_size_of_trend)
                    );
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

        configLikeAndCommentCount();

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_like.setEnabled(false);
                new LikeTrendThread(mHandler, trend.id, !trend.isLiked, 0).start();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_like.setEnabled(true);
                    }
                }, 10000);
            }
        });
    }

    private void configLikeAndCommentCount(){
        iv_like.setEnabled(true);
        tv_like_count.setText(trend.likeCount + "");
        tv_comment_count.setText(trend.commentCount + "");

        if(trend.isLiked){
            iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.liked));
        } else{
            iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CODE_CHOOSE_GYM:
                    choosedGym = (ChoosedGym) data.getSerializableExtra(ChooseGymActivity.CHOOSED_GYM);
                    tv_gym_name.setText(choosedGym.getGymName());
                    break;
            }
        }
    }

    private MyHandler mHandler;
    private final int KEY_COMMENT_SUC = 101;
    private final int KEY_GET_COMMENT_SUC = 201;
    private final int KEY_GET_TREND_DETAIL_SUC = 301;
    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_COMMENT_SUC:
                    Trend.Response12004.Data data = (Trend.Response12004.Data) msg.obj;
                    if(refresh){
                        comments.clear();
                    } else{
                        adapter.setLoadingMore(false);
                    }

                    maxCountPerPage = data.maxCountPerPage;
                    if (maxCountPerPage > data.comments.length){
                        canLoadingMore = false;
                        adapter.setCannotLoadingMore();
                    } else{
                        canLoadingMore = true;
                    }

                    comment_index ++;

                    int lastSize = comments.size();
                    comments.addAll(ArrayUtil.Array2List(data.comments));
                    if (lastSize < comments.size()) {

                        if(refresh){
                            adapter.notifyDataSetChanged();
                        } else{
                            adapter.notifyItemRangeInserted(adapter.headViews.size() + lastSize, data.comments.length);
                        }
                    }
                    refresh = false;
                    break;

                case KEY_COMMENT_SUC:
                    sendToast("评论成功");
                    if(!canLoadingMore){
                        // 如果评论数量过少，则重新加载评论
                        refresh = true;
                        comment_index = 0;
                        new GetCommentThread().start();
                    }
                    trend.commentCount ++;
                    configLikeAndCommentCount();
                    et_comment_text.setText("");
                    break;

                case KEY_LIKE_TREND:
                    trend.isLiked = true;
                    trend.likeCount ++;
                    configLikeAndCommentCount();
                    break;

                case KEY_UNLIKE_TREND:
                    trend.isLiked = false;
                    trend.likeCount --;
                    configLikeAndCommentCount();
                    break;

                case KEY_GET_TREND_DETAIL_SUC:
                    trend = (Common.Trend) msg.obj;
                    configView();
                    break;
            }
        }
    }

    class CommentThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 12006;
            Trend.Request12006 request = new Trend.Request12006();
            Trend.Request12006.Params params = new Trend.Request12006.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.content = et_comment_text.getText().toString();
            params.trendId = trend.id;

            if (!commentToTrend){
                params.toUserId = toBriefUser.userId;
                params.toCommentId = toCommentId;
            }

            if (choosedGym != null){
                params.gymId = choosedGym.getGymId();
            }

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_COMMENT_TREND, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Trend.Response12006 response = Trend.Response12006.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_COMMENT_SUC;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }

    class GetCommentThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 12004;
            Trend.Request12004 request = new Trend.Request12004();
            Trend.Request12004.Params params = new Trend.Request12004.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.pageIndex = comment_index;
            params.trendId = trend.id;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_COMMENT, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Trend.Response12004 response = Trend.Response12004.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.obj = response.data;
                            msg.what = KEY_GET_COMMENT_SUC;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }

    class GetTrendByIdThread extends Thread{
        private int trendId;
        public GetTrendByIdThread(int trendId){
            this.trendId = trendId;
        }

        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 12003;
            Trend.Request12003 request = new Trend.Request12003();
            Trend.Request12003.Params params = new Trend.Request12003.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.trendId = trendId;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_TREND_BY_ID, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Trend.Response12003 response = Trend.Response12003.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_GET_TREND_DETAIL_SUC;
                            msg.obj = response.data.trend;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }
}
