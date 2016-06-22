package com.xiang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.LocationUtil;
import com.xiang.Util.SportTimeUtil;
import com.xiang.Util.SportXIntent;
import com.xiang.adapter.CoverGymItemAdapter;
import com.xiang.adapter.UserInGymAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.database.helper.GymScoreHelper;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.gym.nano.Gym;
import com.xiang.proto.nano.Common;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.sportx.ImageAndTextActivity;
import com.xiang.sportx.R;
import com.xiang.thread.GetGymListThread;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 祥祥 on 2016/4/26.
 */
public class GymFragment extends Fragment implements BDLocationListener{

    private View mView;
    private RecyclerView rv_user_in_gym, rv_gyms;
    private ImageView iv_avatar;
    private TextView tv_gym_name, tv_userCount, tv_trendCount, tv_recommend;
    private TextView tv_equipment;
    private TextView tv_recommend_content;
    private RelativeLayout rl_gym;

    // view
    private View preferGymView;

    // adapter
    private UserInGymAdapter adapter;
    private CoverGymItemAdapter gymItemAdapter;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();
    private GymScoreHelper gymScoreHelper;

    // data
    private Location location;
    private List<Common.BriefGym> briefGyms = new ArrayList<>();
    private List<Common.BriefUser> briefUsers = new ArrayList<>();
    private int pageIndex = 0;
    // sp
    private SharedPreferences sp;

    private long lastCheckInTime = 0;

    // handler
    private MyHandler mHandler;

    // thread
    private CheckThread checkThread;
    private boolean isUpdate = true;

    // 自动刷新
    private long lastRefreshRecommendTime = 0;
    private final long RefreshIntervalTime = SportTimeUtil.HOUR;
    private boolean needRefreshRecommendForce = false;

    private LocationClient mLocationClient;

    protected void onInitFragment() {

        sp = getContext().getSharedPreferences(Constant.SP_DATA, Context.MODE_PRIVATE);
        mHandler = new MyHandler(getContext(), null);

        rv_gyms = (RecyclerView) findViewById(R.id.rv_gyms);

        rv_user_in_gym = (RecyclerView) findHeadViewById(R.id.rv_user_in_gym);
        iv_avatar = (ImageView) findHeadViewById(R.id.iv_gym_avatar);
        tv_gym_name = (TextView) findHeadViewById(R.id.tv_gym_name);
        tv_userCount = (TextView) findHeadViewById(R.id.tv_renqi);
        tv_trendCount = (TextView) findHeadViewById(R.id.tv_trend_count);
        tv_recommend = (TextView) findHeadViewById(R.id.textView1); // 常用 推荐

        tv_equipment = (TextView) findHeadViewById(R.id.tv_equipment);
        rl_gym = (RelativeLayout) findHeadViewById(R.id.rl_content);

        initRecyclerView();

        initGymList();

        rl_gym.requestFocus();

        mLocationClient = new LocationClient(getContext().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(this);    //注册监听函数
        mLocationClient.setLocOption(LocationUtil.getLocationClientOption());
        mLocationClient.start();
    }

    /**
     * headview find view by id
     * @param id
     * @return
     */
    private View findHeadViewById(int id){
        return preferGymView.findViewById(id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gymScoreHelper = new GymScoreHelper(getContext());
        onInitFragment();
    }

    private void initGymList() {
        gymItemAdapter = new CoverGymItemAdapter(getContext(), briefGyms, rv_gyms);

        // add head view
        gymItemAdapter.addHeadView(preferGymView);

        //TODO add foot view

        gymItemAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportXIntent.gotoGymDetailActivity(getContext(), briefGyms.get(position).id);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv_gyms.setAdapter(gymItemAdapter);
        rv_gyms.setLayoutManager(manager);
        rv_gyms.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!gymItemAdapter.canLoadingMore()) {
                    return;
                }

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (gymItemAdapter.isLoadingMore()) {
                        Log.d("isloadingmore", "ignore manually update!");
                    } else {
                        gymItemAdapter.setLoadingMore(true);

                        new GetGymListThread(mHandler, location, pageIndex).start();
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        adapter = new UserInGymAdapter(getContext(), briefUsers, rv_user_in_gym);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportXIntent.gotoUserDetail(getContext(), briefUsers.get(position).userId, briefUsers.get(position).userName);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rv_user_in_gym.setAdapter(adapter);
        rv_user_in_gym.setLayoutManager(manager);
    }

    private void initRecommendGym(final Common.BriefGym briefGym, int userCount, int trendCount){
        imageLoader.displayImage(briefGym.gymCover, iv_avatar, options);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageAndTextActivity.class);
                intent.putExtra(Constant.IMAGES, new String[]{briefGym.gymCover});
                startActivity(intent);
            }
        });
        tv_gym_name.setText(briefGym.gymName);
        // 设置器材内容
        tv_equipment.setText(briefGym.eqm);

        tv_userCount.setText(userCount + "");
        tv_trendCount.setText(trendCount + "");

        rl_gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SportXIntent.gotoGymDetailActivity(getContext(), briefGym.id);
            }
        });
    }

    private boolean needRefreshRecommendGym(){
        if (needRefreshRecommendForce || System.currentTimeMillis() - lastRefreshRecommendTime > RefreshIntervalTime){
            Log.d("needRefreshR", "true. needRefreshRecommendForce:" + needRefreshRecommendForce);
            return true;
        }
        Log.d("needRefreshR", "false");
        return false;
    }

    private void refreshRecommendGym(){
        lastRefreshRecommendTime = System.currentTimeMillis();
        new GetRecommendGym().start();
    }

    public void refreshIfNeed(){
        // 是否需要刷新推荐场馆
        if (needRefreshRecommendGym()){
            try {
                refreshRecommendGym();
            } catch (Exception e){

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState);
        preferGymView = inflater.inflate(R.layout.view_prefer_gym, null, false);
        mView = inflater.inflate(R.layout.view_gym, container, false);
        return mView;
    }

    private View findViewById(int id){
        return mView.findViewById(id);
    }


    private static final int KEY_UPDATE_TIME = 998;
    private static final int KEY_CHECK_OUT = 999;
    private static final int KEY_GET_RECOMMEND_GYM_SUC = 101;
    private static final int KEY_GET_HIGH_SCORE_GYM_SUC = 102;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        location = new Location("baidu");
        location.setLongitude(bdLocation.getLongitude());
        location.setLatitude(bdLocation.getLatitude());

        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果

        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果

        } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
        } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
            location = null;
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            location = null;
        } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
            location = null;
        }

        if (null != location){
            Log.d("location", location.getLatitude() + "" + location.getLongitude());
        } else{
            final MaterialDialog materialDialog = new MaterialDialog(getContext());
            materialDialog.setCanceledOnTouchOutside(true);
            materialDialog.setTitle("提示");
            materialDialog.setMessage("请您打开“设置->应用->SportX->权限管理”，并允许使用手机定位，以便给您提供更好的服务。");
            materialDialog.setPositiveButton("好的", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }

        new GetGymListThread(mHandler, location, pageIndex).start();
        new GetRecommendGym().start();

        lastRefreshRecommendTime = System.currentTimeMillis();
        mLocationClient.stop();
    }

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
                case KEY_GET_GYM_LIST_SUC:
                    Gym.Response13001.Data data = (Gym.Response13001.Data) msg.obj;

                    int lastSize = briefGyms.size();

                    briefGyms.addAll(ArrayUtil.Array2List(data.briefGyms));

                    if (data.maxCountPerPage > data.briefGyms.length){
                        gymItemAdapter.setCannotLoadingMore();
                    }

                    if(data.briefGyms.length > 0) {
                        gymItemAdapter.notifyItemRangeInserted(lastSize + gymItemAdapter.headViews.size(), data.briefGyms.length);
                    }
                    gymItemAdapter.setLoadingMore(false);
                    pageIndex ++;
                    break;

                case KEY_GET_RECOMMEND_GYM_SUC:
                case KEY_GET_HIGH_SCORE_GYM_SUC:
                    Gym.Response13003.Data data_recommend = (Gym.Response13003.Data) msg.obj;
                    briefUsers.clear();
                    briefUsers.addAll(ArrayUtil.Array2List(data_recommend.briefUsers));
                    adapter.notifyDataSetChanged();
                    initRecommendGym(data_recommend.briefGym, data_recommend.userNum, data_recommend.trendNum);
                    break;
                default:
                    break;
            }

            switch (msg.what){
                case KEY_GET_RECOMMEND_GYM_SUC:
                    tv_recommend.setText("推荐");
                    break;
                case KEY_GET_HIGH_SCORE_GYM_SUC:
                    tv_recommend.setText("常用");
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

    class GetRecommendGym extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 13003;
            Gym.Request13003 request = new Gym.Request13003();
            Gym.Request13003.Params params = new Gym.Request13003.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            int gymId = gymScoreHelper.getHighScoreGymId();
            int SUC_KEY = KEY_GET_RECOMMEND_GYM_SUC;
            if (gymId != 0){
                params.gymId = gymId;
                SUC_KEY = KEY_GET_HIGH_SCORE_GYM_SUC;
            }

            if (location != null){
                params.latitude = (float) location.getLatitude();
                params.longitude = (float) location.getLongitude();
            }

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_RECOMMEND_GYM, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            needRefreshRecommendForce = true;
            if (null != result){
                // 加载成功
                try{
                    Gym.Response13003 response = Gym.Response13003.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = SUC_KEY;
                            msg.obj = response.data;
                            mHandler.sendMessage(msg);
                            needRefreshRecommendForce = false;
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
