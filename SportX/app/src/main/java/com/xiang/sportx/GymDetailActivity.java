package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.SportXIntent;
import com.xiang.adapter.GymImageAdapter;
import com.xiang.adapter.TrendAdapter;
import com.xiang.adapter.UserInGymAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.dafault.DefaultUtil;
import com.xiang.database.helper.GymScoreHelper;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.gym.nano.Gym;
import com.xiang.proto.nano.Common;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.thread.LikeTrendThread;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

public class GymDetailActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RecyclerView rv_trend_in_gym, rv_gym_images, rv_user_in_gym;

    private View headView;
    private TextView tv_gym_name, tv_place;
    private TextView tv_equipment_more, tv_class_more, tv_card_more, tv_intro;
    private ImageView iv_avatar;

    // adapter
    private GymImageAdapter gymImageAdapter;
    private TrendAdapter trendAdapter;
    private UserInGymAdapter userAdapter;

    // data
    private Common.DetailGym detailGym = DefaultUtil.getDetailGym();
    private List<Common.BriefUser> briefUsers = new ArrayList<>();
    private List<Common.Trend> trends = new ArrayList<>();
    private int gymId = -1;
    private int pageIndex = 0;

    // tools
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private GymScoreHelper gymScoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_gym_detail);
        gymScoreHelper = new GymScoreHelper(this);

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
        tv_intro = (TextView) findHeadViewById(R.id.tv_recommend_content);

    }

    @Override
    protected void initData() {
        gymId = getIntent().getIntExtra(Constant.GYM_ID, 0);
        mHandler = new MyHandler(this, null);
        gymScoreHelper.whenGetGymDetail(gymId);
    }

    @Override
    protected void configView() {
        myTitleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myTitleBar.setTitle("");
        myTitleBar.setMoreButton(R.mipmap.show_location, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(detailGym != null) {
                    SportXIntent.gotoMapActivity(GymDetailActivity.this, detailGym.briefGym.gymName, detailGym.briefGym.latitude, detailGym.briefGym.longitude);
                }
            }
        });

        initTrendRecyclerView();

        initUserRecyclerView();

        showProgress("", true);

        new GetGymDetailThread().start();
        new GetGymTrendThread().start();
    }

    /**
     * 初始化推荐用户列表
     */
    private void initUserRecyclerView() {
        userAdapter = new UserInGymAdapter(this, briefUsers, rv_user_in_gym);
        userAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportXIntent.gotoUserDetail(GymDetailActivity.this, briefUsers.get(position).userId, briefUsers.get(position).userName);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        rv_user_in_gym.setAdapter(userAdapter);
        rv_user_in_gym.setLayoutManager(manager);
    }

    /**
     * 初始化 trend 列表
     */
    private void initTrendRecyclerView() {
        trendAdapter = new TrendAdapter(this, trends, rv_trend_in_gym, Constant.FROM_GYM_DETAIL);

        // add head view
        trendAdapter.addHeadView(headView);
        trendAdapter.setOnLikeItemClickListener(new TrendAdapter.OnLikeItemClickListener() {
            @Override
            public void itemLikeClick(final View view, int dataIndex, boolean isLike) {
                view.setEnabled(false);
                new LikeTrendThread(mHandler, trends.get(dataIndex).id, isLike, dataIndex).start();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 10000);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_trend_in_gym.setAdapter(trendAdapter);
        rv_trend_in_gym.setLayoutManager(manager);
        rv_trend_in_gym.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ( ! trendAdapter.canLoadingMore()) {
                    return;
                }

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (trendAdapter.isLoadingMore()) {
                        Log.d("isloadingmore", "ignore manually update!");
                    } else {
                        trendAdapter.setLoadingMore(true);
                        new GetGymTrendThread().start();
                    }
                }
            }
        });
    }

    // 建设房水平滑动图片列表
    private void initImageRecyclerView() {
        gymImageAdapter = new GymImageAdapter(this, ArrayUtil.Array2List(detailGym.gymCovers), rv_gym_images);
        gymImageAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(GymDetailActivity.this, ImageAndTextActivity.class);
                intent.putExtra(Constant.IMAGES, detailGym.gymCovers);
                intent.putExtra(Constant.CURRENT_INDEX, position);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rv_gym_images.setAdapter(gymImageAdapter);
        rv_gym_images.setLayoutManager(manager);
    }

    // 初始化gym详细信息
    private void initDetailGym() {
        myTitleBar.setTitle(detailGym.briefGym.gymName);
        imageLoader.displayImage(detailGym.briefGym.gymAvatar, iv_avatar, options);
        tv_gym_name.setText(detailGym.briefGym.gymName);
        tv_place.setText(detailGym.briefGym.place);

        // 设置器材内容
        tv_equipment_more.setText(detailGym.briefGym.eqm);

        // 设置课程内容
        tv_class_more.setText(detailGym.courses);

        // 设置卡片内容
        tv_card_more.setText(detailGym.gymCards);

        // gym简介
        tv_intro.setText(detailGym.gymIntro);

    }

    private View findHeadViewById(int id){
        return headView.findViewById(id);
    }

    private MyHandler mHandler;
    private final int KEY_GET_GYM_DETAIL_SUC = 101;
    private final int KEY_GET_GYM_TREND_SUC = 102;
    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_GYM_DETAIL_SUC:
                    Gym.Response13002.Data data = (Gym.Response13002.Data) msg.obj;
                    detailGym = data.detailGym;

                    if (detailGym == null){
                        sendToast("该场馆不存在");
                        finish();
                        return ;
                    }

                    briefUsers.addAll(ArrayUtil.Array2List(data.briefUsers));
                    userAdapter.notifyDataSetChanged();

                    initDetailGym();
                    initImageRecyclerView();
                    break;

                case KEY_GET_GYM_TREND_SUC:
                    Gym.Response13004.Data data_trends = (Gym.Response13004.Data) msg.obj;

                    int lastSize = trends.size();

                    trends.addAll(ArrayUtil.Array2List(data_trends.trends));

                    if (data_trends.maxCountPerPage > data_trends.trends.length){
                        // 不能加载更多了
                        trendAdapter.setCannotLoadingMore();
                    }

                    trendAdapter.setLoadingMore(false);

                    if(lastSize == 0){
                        // 重新刷新，否则页面出来后会在底下
                        trendAdapter.notifyDataSetChanged();
                    } else {
                        if (data_trends.trends.length > 0) {
                            trendAdapter.notifyItemRangeInserted(trendAdapter.getHeadViewSize() + lastSize, data_trends.trends.length);
                        }
                    }

                    pageIndex ++;

                    break;

                case KEY_LIKE_TREND:
                    Common.Trend trend = trends.get(msg.arg1);
                    trend.isLiked = true;
                    trend.likeCount ++;
                    trendAdapter.notifyItemLikeChanged(msg.arg1);
                    break;
                case KEY_UNLIKE_TREND:
                    Common.Trend trend1 = trends.get(msg.arg1);
                    trend1.isLiked = false;
                    trend1.likeCount --;
                    trendAdapter.notifyItemLikeChanged(msg.arg1);
                    break;
            }
        }
    }

    class GetGymTrendThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 13004;
            Gym.Request13004 request = new Gym.Request13004();
            Gym.Request13004.Params params = new Gym.Request13004.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.gymId = gymId;
            params.pageIndex = pageIndex;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_GYM_TREND, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            if (null != result){
                // 加载成功
                try{
                    Gym.Response13004 response = Gym.Response13004.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_GET_GYM_TREND_SUC;
                            msg.obj = response.data;
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

    class GetGymDetailThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 13002;
            Gym.Request13002 request = new Gym.Request13002();
            Gym.Request13002.Params params = new Gym.Request13002.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.gymId = gymId;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_DETAIL_GYM, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            if (null != result){
                // 加载成功
                try{
                    Gym.Response13002 response = Gym.Response13002.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_GET_GYM_DETAIL_SUC;
                            msg.obj = response.data;
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
