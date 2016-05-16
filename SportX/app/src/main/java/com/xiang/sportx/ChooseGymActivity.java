package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiang.adapter.BriefGymAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.model.ChoosedGym;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;

import java.util.List;

public class ChooseGymActivity extends BaseAppCompatActivity {

    public static final String CHOOSED_GYM = "choosedgym";

    private MyTitleBar titleBar;
    private RecyclerView rv_gyms;

    // adapter
    private BriefGymAdapter adapter;

    // data
    private List<Common.BriefGym> briefGyms = DefaultUtil.getGyms(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_choose_gym);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        rv_gyms = (RecyclerView) findViewById(R.id.rv_gyms);
    }

    @Override
    protected void initData() {

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
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_gyms.setAdapter(adapter);
        rv_gyms.setLayoutManager(manager);
    }
}
