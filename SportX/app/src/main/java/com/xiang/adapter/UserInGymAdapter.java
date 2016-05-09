package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/4/27.
 */
public class UserInGymAdapter extends RecyclerView.Adapter<UserInGymAdapter.MyViewHolder> {

    private Context context;
    private List<Common.BriefUser> briefUsers;
    private RecyclerView recyclerView;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    public UserInGymAdapter(Context context, List<Common.BriefUser> briefUsers, RecyclerView recyclerView) {
        this.context = context;
        this.briefUsers = briefUsers;
        this.recyclerView = recyclerView;
    }

    private OnRclViewItemClickListener onRclViewItemClickListener;
    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener){
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_user_in_gym, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Common.BriefUser briefUser = briefUsers.get(position);
        if(position == 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.iv_avatar.getLayoutParams();
            layoutParams.leftMargin = 0;
            holder.iv_avatar.setLayoutParams(layoutParams);
        }
        imageLoader.displayImage(briefUser.userAvatar, holder.iv_avatar, options);
        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
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
        return briefUsers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView iv_avatar;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }

}
