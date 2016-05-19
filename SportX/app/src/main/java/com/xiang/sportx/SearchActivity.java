package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.xiang.Util.Constant;
import com.xiang.adapter.GymItemAdapter;
import com.xiang.adapter.HotKeywordsAdapter;
import com.xiang.adapter.KeywordsAdapter;
import com.xiang.adapter.SearchedUserAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends BaseAppCompatActivity {

    private ImageView iv_back;
    private MaterialEditText et_content;
    private TextView tv_search, tv_clear, tv_gym, tv_user;
    private RecyclerView rv_keywords, rv_hot_keywords, rv_searched_gym, rv_searched_user;
    private RelativeLayout rl_searched;

    // adapter
    private KeywordsAdapter historyAdapter;
    private HotKeywordsAdapter hotKeywordsAdapter;
    private GymItemAdapter gymItemAdapter;
    private SearchedUserAdapter searchedUserAdapter;

    // data
    private List<String> hotKeywords;
    private List<String> keywords;
    private Set<String> keywordsSet;
    private List<Common.BriefGym> briefGyms = DefaultUtil.getGyms(20);
    private List<Common.SearchedUser> searchedUsers = DefaultUtil.getSearchedUsers(20);

    private static final int SEARCH_GYM = 0, SEARCH_USER = 1;
    private int currentSearch = SEARCH_GYM;

    // sp
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_search);

        sp = getSharedPreferences(Constant.SP_DATA, MODE_PRIVATE);
        keywordsSet = sp.getStringSet(Constant.SP_KEYWORDS, new HashSet<String>());


        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_content = (MaterialEditText) findViewById(R.id.et_content);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_gym = (TextView) findViewById(R.id.tv_search_gym);
        tv_user = (TextView) findViewById(R.id.tv_search_user);
        rv_keywords = (RecyclerView) findViewById(R.id.rv_keywords);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        rv_hot_keywords = (RecyclerView) findViewById(R.id.rv_hot_keywords);
        rv_searched_gym = (RecyclerView) findViewById(R.id.rv_search_gym);
        rv_searched_user = (RecyclerView) findViewById(R.id.rv_search_user);
        rl_searched = (RelativeLayout) findViewById(R.id.rl_searched);

        rl_searched.setVisibility(View.GONE);

        Drawable drawable = getResources().getDrawable(R.mipmap.search);
        int dimen = (int) getResources().getDimension(R.dimen.search_drawable_size);
        drawable.setBounds(dimen, dimen, dimen, dimen);
        et_content.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    protected void initData() {
        keywords = new ArrayList<>();
        keywords.addAll(keywordsSet);

        hotKeywords = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            hotKeywords.add("热门关键字" + i);
        }
    }

    @Override
    protected void configView() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });



        configHistoryKeywords();

        configHotKeywords();

        configSearchedGym();

        configSearchedUser();

        tv_gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_gym.setBackgroundColor(getResources().getColor(R.color.white));
                tv_user.setBackgroundColor(getResources().getColor(R.color.white_pressed));
                currentSearch = SEARCH_GYM;

                rv_searched_user.setVisibility(View.GONE);
                rv_searched_gym.setVisibility(View.VISIBLE);
            }
        });

        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_user.setBackgroundColor(getResources().getColor(R.color.white));
                tv_gym.setBackgroundColor(getResources().getColor(R.color.white_pressed));
                currentSearch = SEARCH_USER;
                rv_searched_user.setVisibility(View.VISIBLE);
                rv_searched_gym.setVisibility(View.GONE);
            }
        });
    }

    private void doSearch(){
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        keywordsSet.add(et_content.getText().toString());
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(Constant.SP_KEYWORDS, keywordsSet);
        editor.commit();

        updateKeywordsList();

        rl_searched.setVisibility(View.VISIBLE);
        switch (currentSearch){
            case SEARCH_GYM:
                rv_searched_gym.setVisibility(View.VISIBLE);
                rv_searched_user.setVisibility(View.GONE);
                break;
            case SEARCH_USER:
                rv_searched_gym.setVisibility(View.GONE);
                rv_searched_user.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void configSearchedUser() {
        searchedUserAdapter = new SearchedUserAdapter(this, searchedUsers, rv_searched_user);
        searchedUserAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(SearchActivity.this, UserDetailActivity.class));
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_searched_user.setAdapter(searchedUserAdapter);
        rv_searched_user.setLayoutManager(layoutManager);
    }

    private void configSearchedGym() {
        gymItemAdapter = new GymItemAdapter(this, briefGyms, rv_searched_gym);
        gymItemAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(SearchActivity.this, GymDetailActivity.class));
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_searched_gym.setAdapter(gymItemAdapter);
        rv_searched_gym.setLayoutManager(layoutManager);

    }

    private void configHistoryKeywords() {
        historyAdapter = new KeywordsAdapter(this, keywords, rv_keywords);
        historyAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                et_content.setText(keywords.get(position));
                doSearch();
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        rv_keywords.setAdapter(historyAdapter);
        rv_keywords.setLayoutManager(gridLayoutManager);
    }

    private void configHotKeywords() {
        hotKeywordsAdapter = new HotKeywordsAdapter(SearchActivity.this, hotKeywords, rv_hot_keywords);
        hotKeywordsAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                et_content.setText(hotKeywords.get(position));
                doSearch();
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        rv_hot_keywords.setAdapter(hotKeywordsAdapter);
        rv_hot_keywords.setLayoutManager(layoutManager);
    }


    private void updateKeywordsList() {
        keywords.clear();
        keywords.addAll(keywordsSet);
        historyAdapter.notifyDataSetChanged();
    }

}
