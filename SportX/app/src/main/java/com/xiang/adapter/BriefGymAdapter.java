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
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/14.
 */
public class BriefGymAdapter extends RecyclerView.Adapter<BriefGymAdapter.MyViewHolder> {

    private Context context;
    private List<Common.BriefGym> briefGyms;
    private RecyclerView recyclerView;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    public BriefGymAdapter(Context context, List<Common.BriefGym> briefGyms, RecyclerView recyclerView) {
        this.context = context;
        this.briefGyms = briefGyms;
        this.recyclerView = recyclerView;
    }

    private OnRclViewItemClickListener onRclViewItemClickListener;

    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_briefgym, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Common.BriefGym briefGym = briefGyms.get(position);

        holder.tv_place.setText(briefGym.place);
        holder.tv_gymname.setText(briefGym.gymName);
        holder.tv_distance.setText((int)(Math.random() * 10) + "千米");

        imageLoader.displayImage(briefGym.gymAvatar, holder.iv_avatar, options);

        holder.rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRclViewItemClickListener != null){
                    onRclViewItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return briefGyms.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout rl_parent;
        public TextView tv_gymname, tv_place, tv_distance;
        public ImageView iv_avatar;

        public MyViewHolder(View itemView) {
            super(itemView);

            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            tv_gymname = (TextView) itemView.findViewById(R.id.tv_gym_name);
            tv_place = (TextView) itemView.findViewById(R.id.tv_place);
            tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);

            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }
}
