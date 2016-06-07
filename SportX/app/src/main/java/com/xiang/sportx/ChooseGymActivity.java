package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.xiang.Util.ArrayUtil;
import com.xiang.Util.LocationUtil;
import com.xiang.adapter.BriefGymAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.model.ChoosedGym;
import com.xiang.proto.gym.nano.Gym;
import com.xiang.proto.nano.Common;
import com.xiang.thread.GetGymListThread;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class ChooseGymActivity extends BaseAppCompatActivity {

    public static final String CHOOSED_GYM = "choosedgym";

    private MyTitleBar titleBar;
    private RecyclerView rv_gyms;

    // adapter
    private BriefGymAdapter adapter;

    // data
    private List<Common.BriefGym> briefGyms = new ArrayList<>();
    private int pageIndex = 0;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_choose_gym);
        mHander = new MyHandler(this, null);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        rv_gyms = (RecyclerView) findViewById(R.id.rv_gyms);
    }

    @Override
    protected void initData() {
        location = LocationUtil.getLocation(this);
        if (null != location){
            Log.d("location", location.getLatitude() + "" + location.getLongitude());
        } else{
            final MaterialDialog materialDialog = new MaterialDialog(this);
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
    }

    @Override
    protected void configView() {
        titleBar.setBackButtonDefault();
        titleBar.setMoreButton(0, false, null);
        titleBar.setTitle("选择位置");

        adapter = new BriefGymAdapter(this, briefGyms, rv_gyms);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                Common.BriefGym briefGym = briefGyms.get(position);
                ChoosedGym choosedGym = new ChoosedGym(briefGym.id, briefGym.gymName);
                intent.putExtra(CHOOSED_GYM, choosedGym);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        adapter.setOnItemNameAndAvatarClickListener(new BriefGymAdapter.OnItemNameAndAvatarClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(ChooseGymActivity.this, GymDetailActivity.class));
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_gyms.setAdapter(adapter);
        rv_gyms.setLayoutManager(manager);

        rv_gyms.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!adapter.canLoadingMore()) {
                    return;
                }

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 2 && (dy > 0 || lastVisibleItem == totalItemCount - 1)) { // 有些列表高度小于容器高度，就没法滑动了
                    if (adapter.isLoadingMore()) {
                        Log.d("isloadingmore", "ignore manually update!");
                    } else {
                        adapter.setLoadingMore(true);

                        new GetGymListThread(mHander, location, pageIndex).start();
                    }
                }
            }
        });

        showProgress("", true);
        new GetGymListThread(mHander, location, pageIndex).start();
    }

    private MyHandler mHander;

    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_GYM_LIST_SUC:
                    Gym.Response13001.Data data = (Gym.Response13001.Data) msg.obj;

                    int lastSize = briefGyms.size();

                    briefGyms.addAll(ArrayUtil.Array2List(data.briefGyms));

                    if (data.maxCountPerPage > data.briefGyms.length){
                        adapter.setCannotLoadingMore();
                    }

                    if(data.briefGyms.length > 0) {
                        adapter.notifyItemRangeInserted(lastSize, data.briefGyms.length);
                    }
                    adapter.setLoadingMore(false);
                    pageIndex ++;
                    break;
            }
        }
    }
}
