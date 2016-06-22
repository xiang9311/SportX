package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.SportXIntent;
import com.xiang.Util.UserInfoUtil;
import com.xiang.Util.UserStatic;
import com.xiang.adapter.TrendAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.database.helper.BriefUserHelper;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.thread.GetTrendThread;
import com.xiang.thread.LikeTrendThread;
import com.xiang.view.MyTitleBar;
import com.xiang.view.TwoOptionMaterialDialog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

public class UserDetailActivity extends BaseAppCompatActivity {

    private RecyclerView rv_trend;
    private View headerView;
    private MyTitleBar titleBar;

    // headview中的内容
    private ImageView iv_avatar;
    private TextView tv_username;
    private TextView tv_follow, tv_chat;       // 关注的按钮
    private TextView tv_sign;         // 签名
    private ImageView iv_sex;
    private TextView tv_guanzhu_count, tv_fensi_count, tv_trend_count;
    private LinearLayout ll_guanzhu, ll_fensi, ll_trend;

    private TwoOptionMaterialDialog md_login_register;
    // adapter
    private TrendAdapter trendAdapter;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions = DisplayOptionsFactory.createNormalImageOption();

    // data
    private Common.DetailUser detailUser;
    private List<Common.Trend> trends = new ArrayList<>();
    private int userId = 0;
    private String userName = "";

    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_user_detail);

        rv_trend = (RecyclerView) findViewById(R.id.rv_user_trend);
        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        initHeadView();
    }

    private void initHeadView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.view_user_detail_head, null, false);

        iv_avatar = (ImageView) findHeadViewById(R.id.iv_avatar);
        tv_username = (TextView) findHeadViewById(R.id.tv_username);
        tv_follow = (TextView) findHeadViewById(R.id.tv_follow);
        tv_chat = (TextView) findHeadViewById(R.id.tv_chat);
        tv_sign = (TextView) findHeadViewById(R.id.tv_sign);
        iv_sex = (ImageView) findHeadViewById(R.id.iv_sex);
        tv_guanzhu_count = (TextView) findHeadViewById(R.id.tv_guanzhu_count);
        tv_fensi_count = (TextView) findHeadViewById(R.id.tv_fensi_count);
        tv_trend_count = (TextView) findHeadViewById(R.id.tv_trend_count);
        ll_guanzhu = (LinearLayout) findHeadViewById(R.id.ll_guanzhu);
        ll_fensi = (LinearLayout) findHeadViewById(R.id.ll_fensi);
        ll_trend = (LinearLayout) findHeadViewById(R.id.ll_trend);
    }

    private View findHeadViewById(int id){
        return headerView.findViewById(id);
    }

    @Override
    protected void initData() {
        userId = getIntent().getIntExtra(Constant.USER_ID, 0);
        userName = getIntent().getStringExtra(Constant.USER_NAME);
    }

    @Override
    protected void configView() {
        // title
        titleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitle("个人详情");
        titleBar.setMoreButton(0, false, null);

        tv_username.setText(userName);

        // headview
//        configHeadView();

        trendAdapter = new TrendAdapter(this, trends, rv_trend, Constant.FROM_USER_DETAIL);
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
                },10000);
            }
        });

        trendAdapter.addHeadView(headerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_trend.setAdapter(trendAdapter);
        rv_trend.setLayoutManager(layoutManager);

        mHandler = new MyHandler(this, null);

        new GetUserDetailThread().start();
    }

    private void configHeadView() {
        imageLoader.displayImage(detailUser.userAvatar, iv_avatar, displayImageOptions);
        tv_username.setText(detailUser.userName);
        tv_sign.setText(detailUser.sign);

        tv_guanzhu_count.setText("" + detailUser.guanzhuCount);
        tv_fensi_count.setText("" + detailUser.fensiCount);
        tv_trend_count.setText("" + detailUser.trendCount);

        int height = tv_username.getLineHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_sex.getLayoutParams();
        layoutParams.height = height;
        iv_sex.setLayoutParams(layoutParams);

        if(detailUser.sex == Common.MALE){
            iv_sex.setImageDrawable(getResources().getDrawable(R.mipmap.male));
        } else {
            iv_sex.setImageDrawable(getResources().getDrawable(R.mipmap.female));
        }

//        tv_follow
        if(detailUser.isFollowed){
            tv_follow.setText("取消关注");
        } else{
            tv_follow.setText("+ 关注");
        }
        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserStatic.logged) {
                    new GuanzhuThread(!detailUser.isFollowed).start();
                }  else{
                    if(md_login_register == null){
                        md_login_register = MaterialDialogFactory.createLoginOrRegisterMd(UserDetailActivity.this);
                    }
                    md_login_register.show();
                }
            }
        });
        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动会话界面
                if (UserStatic.logged) {
                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().startPrivateChat(UserDetailActivity.this, userId + "", userName);
                }  else{
                    if(md_login_register == null){
                        md_login_register = MaterialDialogFactory.createLoginOrRegisterMd(UserDetailActivity.this);
                    }
                    md_login_register.show();
                }
            }
        });

        ll_guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SportXIntent.gotoUserListActivity(UserDetailActivity.this, userId, userName, Constant.FIND_GUANZHU);
            }
        });

        ll_fensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SportXIntent.gotoUserListActivity(UserDetailActivity.this, userId, userName, Constant.FIND_FENSI);
            }
        });

        ll_trend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayoutManager)rv_trend.getLayoutManager()).scrollToPositionWithOffset(1, 0);
            }
        });

        rv_trend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!trendAdapter.canLoadingMore()) {
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
                        new GetTrendThread(mHandler, pageIndex, userId).start();
                    }
                }
            }
        });

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] images = new String[]{detailUser.userAvatar};
                Intent intent = new Intent(UserDetailActivity.this, ImageAndTextActivity.class);
                intent.putExtra(Constant.IMAGES, images);
                intent.putExtra(Constant.SHOW_INDICATOR, false);
                startActivity(intent);
            }
        });
    }

    private MyHandler mHandler;
    private static final int KEY_GUANZHU_SUC = 101;
    private static final int KEY_CANCLE_GUANZHU_SUC = 102;
    private static final int KEY_GET_USER_DETAIL_SUC = 103;

    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GUANZHU_SUC:
                    Snackbar.make(rv_trend, "关注成功", Snackbar.LENGTH_SHORT).show();
                    tv_follow.setText("取消关注");
                    detailUser.isFollowed = true;
                    break;
                case KEY_CANCLE_GUANZHU_SUC:
                    Snackbar.make(rv_trend, "取消成功", Snackbar.LENGTH_SHORT).show();
                    tv_follow.setText("+ 关注");
                    detailUser.isFollowed = false;
                    break;
                case KEY_GET_USER_DETAIL_SUC:
                    configHeadView();
                    int lastSize = trends.size();
                    trends.addAll(ArrayUtil.Array2List(detailUser.trends));
                    if (detailUser.trendMaxCountPerPage > detailUser.trends.length){
                        // 不能加载更多了
                        trendAdapter.setCannotLoadingMore();
                    }
                    if(lastSize < trends.size()) {
                        trendAdapter.notifyDataSetChanged();
                    }
                    pageIndex ++;

                    UserInfoUtil.updateSavedUserInfo(detailUser.userId, detailUser.userName, detailUser.userAvatar, new BriefUserHelper(UserDetailActivity.this));

                    break;

                case KEY_GET_TREND_LIST_SUC:
                    Pilot.Response10005.Data data = (Pilot.Response10005.Data) msg.obj;
                    trendAdapter.setLoadingMore(false);
                    if (data.maxCountPerPage > data.trends.length){
                        // 不能加载更多了
                        trendAdapter.setCannotLoadingMore();
                    }
                    int lastSize0 = trends.size();
                    trends.addAll(ArrayUtil.Array2List(data.trends));
                    if(lastSize0 < trends.size()) {
                        trendAdapter.notifyItemRangeInserted(lastSize0 + trendAdapter.getHeadViewSize(), data.trends.length);
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

    class GetUserDetailThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10012;
            Pilot.Request10012 request = new Pilot.Request10012();
            Pilot.Request10012.Params params = new Pilot.Request10012.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.userId = userId;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_USER_DETAIL, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10012 response = Pilot.Response10012.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){

                            detailUser = response.data.detailUser;

                            Message msg = Message.obtain();
                            msg.what = KEY_GET_USER_DETAIL_SUC;
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

    class GuanzhuThread extends Thread{
        private boolean action_guanzhu = true;
        public GuanzhuThread(boolean action_guanzhu){
            this.action_guanzhu = action_guanzhu;
        }

        @Override
        public void run() {
            super.run();

            long currentMills = System.currentTimeMillis();
            int cmdid = 10011;
            Pilot.Request10011 request = new Pilot.Request10011();
            Pilot.Request10011.Params params = new Pilot.Request10011.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.toUserId = userId;
            params.isFollow = action_guanzhu;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GUANZHU_USER, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10011 response = Pilot.Response10011.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = action_guanzhu ? KEY_GUANZHU_SUC : KEY_CANCLE_GUANZHU_SUC;
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
