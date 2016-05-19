package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/17.
 */
public class HotKeywordsAdapter extends RecyclerView.Adapter<HotKeywordsAdapter.MyViewHolder> {

    private Context context;
    private List<String> keywords;
    private RecyclerView recyclerView;

    public HotKeywordsAdapter(Context context, List<String> keywords, RecyclerView recyclerView) {
        this.context = context;
        this.keywords = keywords;
        this.recyclerView = recyclerView;
    }

    private OnRclViewItemClickListener onRclViewItemClickListener;

    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_hot_keywords, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String keyword = keywords.get(position);

        holder.tv_index.setText(position + 1 + "");
        // 设置宽度
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.tv_index.getLayoutParams();
        int size = (int) context.getResources().getDimension(R.dimen.text_bigger);
        layoutParams.height = size;
        layoutParams.width = size;
        holder.tv_index.setLayoutParams(layoutParams);
        // 设置颜色
        switch (position){
            case 0:
                holder.tv_index.setBackgroundColor(context.getResources().getColor(R.color.hot1));
                break;
            case 1:holder.tv_index.setBackgroundColor(context.getResources().getColor(R.color.hot2));break;
            case 2:holder.tv_index.setBackgroundColor(context.getResources().getColor(R.color.hot3));break;
            default:holder.tv_index.setBackgroundColor(context.getResources().getColor(R.color.hot_normal));break;
        }

        holder.tv_keyword.setText(keyword);
        holder.ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRclViewItemClickListener != null) {
                    onRclViewItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout ll_parent;
        public TextView tv_keyword, tv_index;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_parent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
            tv_keyword = (TextView) itemView.findViewById(R.id.tv_word);
            tv_index = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }
}
