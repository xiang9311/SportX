package com.xiang.sportx;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wefika.flowlayout.FlowLayout;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.SportXIntent;
import com.xiang.Util.UserInfoUtil;
import com.xiang.Util.ViewUtil;
import com.xiang.Util.WindowUtil;
import com.xiang.adapter.CoverGymItemAdapter;
import com.xiang.adapter.HotKeywordsAdapter;
import com.xiang.adapter.KeywordsAdapter;
import com.xiang.adapter.SearchedUserAdapter;
import com.xiang.base.BaseHandler;
import com.xiang.database.helper.BriefUserHelper;
import com.xiang.database.helper.HistoryKeywordHelper;
import com.xiang.database.model.TblHistoryKeyword;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseAppCompatActivity {

    private MaterialEditText et_content;
    private TextView tv_search, tv_clear, tv_gym, tv_user;
    private RecyclerView rv_searched_gym, rv_searched_user;
    private FlowLayout fl_hot_keywords, fl_history_keywords;
    private RelativeLayout rl_searched;

    // adapter
    private KeywordsAdapter historyAdapter;
    private HotKeywordsAdapter hotKeywordsAdapter;
    private CoverGymItemAdapter gymItemAdapter;
    private SearchedUserAdapter searchedUserAdapter;

    // data
    private List<String> hotKeywords;
    private List<TblHistoryKeyword> historyKeywords;
    private List<Common.BriefGym> briefGyms = new ArrayList<>();
    private List<Common.SearchedUser> searchedUsers = new ArrayList<>();
    private String lastKeyword = "";

    private static final int SEARCH_GYM = 0, SEARCH_USER = 1;
    private int currentSearch = SEARCH_GYM;
    private int pageIndex_user = 0, pageIndex_gym = 0;
    private boolean isloading_user = false, isIsloading_gym = false;

    // tools
    private BriefUserHelper briefUserHelper = new BriefUserHelper(this);
    private HistoryKeywordHelper historyKeywordHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_search);

        et_content = (MaterialEditText) findViewById(R.id.et_content);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_gym = (TextView) findViewById(R.id.tv_search_gym);
        tv_user = (TextView) findViewById(R.id.tv_search_user);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        fl_history_keywords = (FlowLayout) findViewById(R.id.fl_history_keywords);
        fl_hot_keywords = (FlowLayout) findViewById(R.id.fl_hot_keywords);
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
        historyKeywordHelper = new HistoryKeywordHelper(this);
        historyKeywords = historyKeywordHelper.getHistoryKeywords();

        hotKeywords = new ArrayList<>();
    }

    @Override
    protected void configView() {
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().length() > 0) {
                    doSearch();
                } else {
                    finish();
                }
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

                startSearchThread(pageIndex_gym == 0);
            }
        });

        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_user.setBackgroundColor(getResources().getColor(R.color.white));
                tv_gym.setBackgroundColor(getResources().getColor(R.color.white_pressed));
                currentSearch = SEARCH_USER;

                startSearchThread(pageIndex_user == 0);
            }
        });

        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyKeywordHelper.removeAll();
                updateHistoryKeywordsList();
            }
        });

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    tv_search.setText("搜索");
                } else {
                    tv_search.setText("取消");
                }
            }
        });

        new GetHotKeywordsThread().start();

        mHandler = new MyHandler(this, null);
    }

    private void startSearchThread(boolean needSearch) {
        switch (currentSearch){
            case SEARCH_GYM:

                if(isIsloading_gym) {
                    return ;
                }

                rv_searched_gym.setVisibility(View.VISIBLE);
                rv_searched_user.setVisibility(View.GONE);
                if(needSearch) {
                    // 清空之前的数据
                    briefGyms.clear();
                    gymItemAdapter.notifyDataSetChanged();
                    showProgress(null, true);
                    new SearchGymThread().start();
                    rv_searched_gym.scrollToPosition(0);
                }
                break;
            case SEARCH_USER:

                if(isloading_user){
                    return;
                }

                rv_searched_gym.setVisibility(View.GONE);
                rv_searched_user.setVisibility(View.VISIBLE);
                if(needSearch) {
                    // searched user 清空
                    searchedUsers.clear();
                    searchedUserAdapter.notifyDataSetChanged();
                    showProgress(null, true);
                    new SearchUserThread().start();
                }
                break;
        }
    }

    private void doSearch(){

        WindowUtil.hideInputMethod(this);

        // 空的话返回
        if(et_content.getText().toString().equals("")){
            return ;
        }

        lastKeyword = et_content.getText().toString();

        historyKeywordHelper.addHistoryKeyword(et_content.getText().toString());

        updateHistoryKeywordsList();

        rl_searched.setVisibility(View.VISIBLE);

        pageIndex_user = 0;
        pageIndex_gym = 0;

        startSearchThread(true);
    }

    private void configSearchedUser() {
        searchedUserAdapter = new SearchedUserAdapter(this, searchedUsers, rv_searched_user);
        searchedUserAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportXIntent.gotoUserDetail(SearchActivity.this, searchedUsers.get(position).userId, searchedUsers.get(position).userName);
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
        gymItemAdapter = new CoverGymItemAdapter(this, briefGyms, rv_searched_gym);
        gymItemAdapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportXIntent.gotoGymDetailActivity(SearchActivity.this, briefGyms.get(position).id);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_searched_gym.setAdapter(gymItemAdapter);
        rv_searched_gym.setLayoutManager(layoutManager);

        rv_searched_gym.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                        new SearchGymThread().start();
                    }
                }
            }
        });
    }

    private void configHistoryKeywords() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        fl_history_keywords.removeAllViews();
        for (int i = 0 ; i < historyKeywords.size(); i ++){
            View view = layoutInflater.inflate(R.layout.textview_keywords, null);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = ViewUtil.dp2px(this, 3);
            layoutParams.topMargin = ViewUtil.dp2px(this, 2);
            textView.setLayoutParams(layoutParams);
            final String keyword = historyKeywords.get(i).keyword;
            textView.setText(keyword);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_content.setText(keyword);
                    doSearch();
                }
            });
            fl_history_keywords.addView(textView);
        }
    }

    private void configHotKeywords() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        fl_hot_keywords.removeAllViews();
        for (int i = 0 ; i < hotKeywords.size(); i ++){
            View view = layoutInflater.inflate(R.layout.textview_keywords, null);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = ViewUtil.dp2px(this, 3);
            layoutParams.topMargin = ViewUtil.dp2px(this, 2);
            textView.setLayoutParams(layoutParams);
            final String keyword = hotKeywords.get(i);
            textView.setText(keyword);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_content.setText(keyword);
                    doSearch();
                }
            });
            fl_hot_keywords.addView(textView);
        }
    }


    private void updateHistoryKeywordsList() {
        historyKeywords.clear();
        historyKeywords.addAll(historyKeywordHelper.getHistoryKeywords());
        configHistoryKeywords();
    }

    private MyHandler mHandler;
    private static final int KEY_SEARCH_USER_SUC = 101;
    private static final int KEY_SEARCH_GYM_SUC = 102;
    private static final int KEY_GET_SEARCH_KEYS_SUC = 103;
    private class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_SEARCH_USER_SUC:
                    Pilot.Response10013.Data data = (Pilot.Response10013.Data) msg.obj;
                    int count = data.maxCountPerPage;
                    int lastUserSize = searchedUsers.size();

                    if (data.searchedUsers.length < data.maxCountPerPage){

                    }

                    searchedUsers.addAll(ArrayUtil.Array2List(data.searchedUsers));
                    if (lastUserSize == 0){
                        searchedUserAdapter.notifyDataSetChanged();
                    } else {
                        searchedUserAdapter.notifyItemRangeInserted(pageIndex_user * count, data.searchedUsers.length);
                    }
                    pageIndex_user ++;

                    for(int i = 0; i < data.searchedUsers.length; i ++){
                        Common.SearchedUser searchedUser = data.searchedUsers[i];
                        UserInfoUtil.updateSavedUserInfo(searchedUser.userId, searchedUser.userName, searchedUser.userAvatar, briefUserHelper);
                    }
                    break;

                case KEY_SEARCH_GYM_SUC:
                    Pilot.Response10014.Data data_gym = (Pilot.Response10014.Data) msg.obj;
                    gymItemAdapter.setLoadingMore(false);
                    int lastGymSize = briefGyms.size();
                    int count_gym = data_gym.maxCountPerPage;

                    if (data_gym.maxCountPerPage > data_gym.briefGyms.length){
                        gymItemAdapter.setCannotLoadingMore();
                    }

                    briefGyms.addAll(ArrayUtil.Array2List(data_gym.briefGyms));
                    if (lastGymSize == 0) {
                        gymItemAdapter.notifyDataSetChanged();
                    } else{
                        gymItemAdapter.notifyItemRangeInserted(pageIndex_gym * count_gym, data_gym.briefGyms.length);
                    }
                    pageIndex_gym ++;
                    break;

                case KEY_GET_SEARCH_KEYS_SUC:
                    Pilot.Response10015.Data data_keywords = (Pilot.Response10015.Data) msg.obj;
                    hotKeywords.clear();
                    hotKeywords.addAll(ArrayUtil.Array2List(data_keywords.keys));
                    configHotKeywords();
                    break;
            }
        }
    }

    class SearchGymThread extends Thread{
        @Override
        public void run() {
            super.run();
            isIsloading_gym = true;

            String keyWord = lastKeyword;

            long currentMills = System.currentTimeMillis();
            int cmdid = 10014;
            Pilot.Request10014 request = new Pilot.Request10014();
            Pilot.Request10014.Params params = new Pilot.Request10014.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.keyword = keyWord;
            params.pageIndex = pageIndex_gym;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_SEARCH_GYM, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            if (null != result){
                // 加载成功
                isIsloading_gym = false;
                try{
                    Pilot.Response10014 response = Pilot.Response10014.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_SEARCH_GYM_SUC;
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

                } catch (Exception e){
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

    class SearchUserThread extends Thread{
        @Override
        public void run() {
            super.run();
            isloading_user = true;

            String keyWord = lastKeyword;

            long currentMills = System.currentTimeMillis();
            int cmdid = 10013;
            Pilot.Request10013 request = new Pilot.Request10013();
            Pilot.Request10013.Params params = new Pilot.Request10013.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.keyword = keyWord;
            params.pageIndex = pageIndex_user;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_SEARCH_USER, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            if (null != result){
                // 加载成功
                isloading_user = false;
                try{
                    Pilot.Response10013 response = Pilot.Response10013.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_SEARCH_USER_SUC;
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

                } catch (Exception e){
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

    // 获取搜索热门关键字
    class GetHotKeywordsThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10015;
            Pilot.Request10015 request = new Pilot.Request10015();
            Pilot.Request10015.Params params = new Pilot.Request10015.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_HOT_SEARCH_KEY, cmdid, currentMills);
            mHandler.sendDisMissProgress();
            if (null != result){
                // 加载成功
                isloading_user = false;
                try{
                    Pilot.Response10015 response = Pilot.Response10015.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            if (response.data != null) {
                                Message msg = Message.obtain();
                                msg.what = KEY_GET_SEARCH_KEYS_SUC;
                                msg.obj = response.data;
                                mHandler.sendMessage(msg);
                            } else{
                                // data is null
                            }
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

                } catch (Exception e){
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
