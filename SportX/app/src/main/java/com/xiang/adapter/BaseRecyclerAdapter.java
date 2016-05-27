package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xiang.Util.ViewUtil;
import com.xiang.sportx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 祥祥 on 2016/5/5.
 *
 */
public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    public static final int MAX_ID = 9999999;

    public Context context;
    public RecyclerView recyclerView;
    public List data;
//    public LinearLayoutManager layoutManager;

    // headview
    public List<View> headViews;
    public List<View> footViews;
    private View loadingMoreView;
    private RelativeLayout rl_loading, rl_loaded;

    public int headMaxSize = 10;
    public int footMaxSize = 10;

    private boolean isLoadingMore = false;
    private boolean canLoadingMore = true;

    public BaseRecyclerAdapter(Context context, List data, final RecyclerView recyclerView) {
        headViews = new ArrayList<>();
        footViews = new ArrayList<>();

        this.context = context;
        this.recyclerView = recyclerView;
        this.data = data;

        loadingMoreView = LayoutInflater.from(context).inflate(R.layout.view_loadding_more, null);
        ViewGroup.LayoutParams layoutParams = loadingMoreView.getLayoutParams();
        if(null == layoutParams) {
            layoutParams = new ViewGroup.LayoutParams(ViewUtil.getWindowWidth(context), (int) context.getResources().getDimension(R.dimen.more_bottom_height));
        }
        layoutParams.width = ViewUtil.getWindowWidth(context);
        loadingMoreView.setLayoutParams(layoutParams);

        rl_loading = (RelativeLayout) loadingMoreView.findViewById(R.id.rl_loading);
        rl_loaded = (RelativeLayout) loadingMoreView.findViewById(R.id.rl_complete);

        footViews.add(loadingMoreView);
    }

    /**
     * 重载此方法须知：需在重载方法里先判断position是否是head或者foot，然后根据getDataByPosition（position）
     * 判断自己的数据type
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if(position < headViews.size()){
            return position + 1;      // 不能为 0 ，0是普通的view    1-10
        }
        if(position >= (getItemCount() - footViews.size())){
            return getItemCount() - footViews.size() - position - 1;    // -1 - -10
        }
        return super.getItemViewType(position);
    }

    /**
     * 根据position获取对应的数据。
     * @param position
     * @return
     */
    public Object getDataByPosition(int position){
        return data.get(position - headViews.size());
    }

    @Override
    public int getItemCount() {
        return data.size() + headViews.size() + footViews.size();
    }

    public boolean hasHead(){
        return headViews.size() > 0;
    }

    public boolean hasFoot(){
        return footViews.size() > 0;
    }

    private void postInvalidateRecyclerView(){
        if(recyclerView != null && recyclerView.getAdapter() != null){
            recyclerView.postInvalidate();
        }
    }

    public void addHeadView(View headView){

        if(this.headViews.size() >= headMaxSize){
            return ;
        }

        int lastSize = this.headViews.size();
        this.headViews.add(headView);
//        postInvalidateRecyclerView();
        notifyItemRangeInserted(lastSize, 1);
    }

    public void addHeadView(View headView, int position){

        if(this.headViews.size() >= headMaxSize){
            return ;
        }

        this.headViews.add(position, headView);
        postInvalidateRecyclerView();
    }

//    public void addFootView(View footView){
//
//        if(this.footViews.size() >= headMaxSize){
//            return ;
//        }
//
//        this.footViews.add(footView);
//        postInvalidateRecyclerView();
//    }

    public void removeHeadView(){
        if (hasHead()) {
            this.notifyItemRemoved(0);
            this.headViews.remove(headViews.size() - 1);
//            postInvalidateRecyclerView();
            notifyItemRemoved(headViews.size());
        }
    }

    public boolean isHeadViewOrFootView(int type){
        return isHeadView(type) || isFootView(type);
    }

    public boolean isHeadView(int type){   // 1-max 包含max，因为是从1开始的
        if(type > 0 && type <= headMaxSize){
            return true;
        }
        return false;
    }

    public boolean isFootView(int type){
        if(type < 0 && type >= -footMaxSize){
            return true;
        }
        return false;
    }

    /**
     * 如果是 headview 或者 footview 则会返回，否则返回null
     * @param type
     * @return
     */
    public View getHeadOrFootViewByType(int type){
        if (isHeadView(type)){
            return getHeadViewByType(type);
        } else if(isFootView(type)){
            return getFootViewByType(type);
        } else {
            return null;
        }
    }

    public int getHeadViewSize(){
        if (headViews == null){
            return 0;
        }
        return headViews.size();
    }

    public View getHeadViewByType(int type){
        try {
            return headViews.get(type - 1);
        } catch (IndexOutOfBoundsException e){
            return headViews.get(0);
        }
    }

    public View getFootViewByType(int type){
        try{
            return footViews.get(-type - 1);
        } catch (IndexOutOfBoundsException e){
            return footViews.get(0);
        }
    }

    public void setLoadingMore(boolean loadingMore){
        isLoadingMore = loadingMore;
        rl_loaded.setVisibility(View.GONE);
        if (loadingMore) {
            rl_loading.setVisibility(View.VISIBLE);
        } else{
            rl_loading.setVisibility(View.GONE);
        }
    }

    public void setCannotLoadingMore(){
        canLoadingMore = false;
        rl_loading.setVisibility(View.GONE);
        rl_loaded.setVisibility(View.VISIBLE);
    }

    public void setCanLoadingMore(){
        canLoadingMore = true;
        rl_loading.setVisibility(View.VISIBLE);
        rl_loaded.setVisibility(View.GONE);
    }

    public boolean canLoadingMore() {
        return canLoadingMore;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }
}
