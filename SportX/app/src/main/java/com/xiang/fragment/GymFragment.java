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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.LocationUtil;
import com.xiang.Util.SportXIntent;
import com.xiang.adapter.CoverGymItemAdapter;
import com.xiang.adapter.UserInGymAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.gym.nano.Gym;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.GymDetailActivity;
import com.xiang.sportx.ImageAndTextActivity;
import com.xiang.sportx.R;
import com.xiang.thread.GetGymListThread;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 祥祥 on 2016/4/26.
 */
public class GymFragment extends Fragment {

    private View mView;
    private RecyclerView rv_user_in_gym, rv_gyms;
    private ImageView iv_avatar;
    private TextView tv_gym_name;
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

    // data
    private Common.DetailGym detailGym = DefaultUtil.getDetailGym();
    private Location location;
    private List<Common.BriefGym> briefGyms = new ArrayList<>();
    private int pageIndex = 0;
    // sp
    private SharedPreferences sp;

    private long lastCheckInTime = 0;

    // handler
    private MyHandler mHandler;

    // thread
    private CheckThread checkThread;
    private boolean isUpdate = true;

    protected void onInitFragment() {
        location = LocationUtil.getLocation(getContext());
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

        sp = getContext().getSharedPreferences(Constant.SP_DATA, Context.MODE_PRIVATE);
        mHandler = new MyHandler(getContext(), null);

        rv_gyms = (RecyclerView) findViewById(R.id.rv_gyms);

        rv_user_in_gym = (RecyclerView) findHeadViewById(R.id.rv_user_in_gym);
        iv_avatar = (ImageView) findHeadViewById(R.id.iv_gym_avatar);
        tv_gym_name = (TextView) findHeadViewById(R.id.tv_gym_name);

        tv_equipment = (TextView) findHeadViewById(R.id.tv_equipment);
        rl_gym = (RelativeLayout) findHeadViewById(R.id.rl_content);

        initRecyclerView();

        initGymDetail();

        initGymList();

        new GetGymListThread(mHandler, location, pageIndex).start();

        rl_gym.requestFocus();
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
                startActivity(new Intent(getContext(), GymDetailActivity.class));
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

    /**
     * 初始化场馆详情信息
     */
    private void initGymDetail() {
        imageLoader.displayImage(detailGym.briefGym.gymCover, iv_avatar, options);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageAndTextActivity.class);
                intent.putExtra(Constant.IMAGES, detailGym.briefGym.gymCover);
                startActivity(intent);
            }
        });
        tv_gym_name.setText(detailGym.briefGym.gymName);
        // 设置器材内容
        tv_equipment.setText(detailGym.briefGym.eqm);

        rl_gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GymDetailActivity.class));
            }
        });

    }

    private void initRecyclerView() {
        adapter = new UserInGymAdapter(getContext(), DefaultUtil.getBriefUsers(8), rv_user_in_gym);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportXIntent.gotoUserDetail(getContext(), 7, "这个是测试的");
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
//        headerView = inflater.inflate(R.layout.view_gyms_header, null, false);
        preferGymView = inflater.inflate(R.layout.view_prefer_gym, null, false);
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
