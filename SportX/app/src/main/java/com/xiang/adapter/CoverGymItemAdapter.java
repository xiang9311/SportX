package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ViewUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/6/2.
 */
public class CoverGymItemAdapter extends BaseRecyclerAdapter<CoverGymItemAdapter.MyViewHolder> {

    private Context context;
    private List<Common.BriefGym> gyms;
    private RecyclerView recyclerView;

    private float height;

    public CoverGymItemAdapter(Context context, List<Common.BriefGym> data, RecyclerView recyclerView) {
        super(context, data, recyclerView);
        this.context = context;
        this.gyms = gyms;
        this.recyclerView = recyclerView;

        int windowWidth = ViewUtil.getWindowWidth(context);
        float space = context.getResources().getDimension(R.dimen.padding_lr) * 2;
        float width = windowWidth - space;
        height = width * 0.6f;
    }

    // item点击事件监听
    private OnRclViewItemClickListener onRclViewItemClickListener;
    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    //tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getHeadOrFootViewByType(viewType);

        if(view != null){
            return new MyViewHolder(view);
        } else {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_gym, parent, false));
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.rl_parent.getLayoutParams();
            layoutParams.height = (int) height;
            holder.rl_parent.setLayoutParams(layoutParams);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(isHeadViewOrFootView(getItemViewType(position))){
            return ;
        }

        Common.BriefGym briefGym = (Common.BriefGym) getDataByPosition(position);

        holder.tv_equipment.setText(briefGym.eqm);
        holder.tv_gymname.setText(briefGym.gymName);
        holder.tv_place.setText(briefGym.place);
        imageLoader.displayImage(briefGym.gymCover, holder.iv_cover, options);

        holder.rl_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRclViewItemClickListener != null) {
                    onRclViewItemClickListener.onItemClick(v, position - headViews.size());
                }
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView iv_cover;
        public TextView tv_gymname;
        public TextView tv_equipment;
        public TextView tv_place;

        public RelativeLayout rl_content, rl_parent;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tv_gymname = (TextView) itemView.findViewById(R.id.tv_gym_name);
            tv_equipment = (TextView) itemView.findViewById(R.id.tv_equipment);
            tv_place = (TextView) itemView.findViewById(R.id.tv_place);
            rl_content = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
        }
    }
}
