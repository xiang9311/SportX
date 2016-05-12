package com.xiang.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;
import com.xiang.sportx.UserDetailActivity;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/12.
 */

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.MyViewHolder> {

    public static final int SPAN_COUNT = 4;

    private Context context;
    private List<Common.BriefUser> briefUsers;
    private RecyclerView recyclerView;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions = DisplayOptionsFactory.createAvatarIconOption();

    private int rl_width, iv_width, rl_padding;           //10 : 7 : 1.5  每行四个

    ViewGroup.LayoutParams layoutParams ;

    public UserItemAdapter(Context context, List<Common.BriefUser> briefUsers, RecyclerView recyclerView) {
        this.context = context;
        this.briefUsers = briefUsers;
        this.recyclerView = recyclerView;

        int windowWidth = ViewUtil.getWindowWidth(context);
        rl_width = windowWidth / SPAN_COUNT;
        iv_width = rl_width * 7 / 10;
        rl_padding = (rl_width - iv_width) / 2;

        layoutParams = new ViewGroup.LayoutParams(rl_width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_user, parent, false);
        MyViewHolder vh = new MyViewHolder(view);

        // 设置高宽及padding
        vh.rl_content.setLayoutParams(layoutParams);
        vh.rl_content.setPadding(rl_padding, rl_padding, rl_padding, rl_padding);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vh.iv_avatar.getLayoutParams();
        lp.width = iv_width;
        lp.height = iv_width;
        vh.iv_avatar.setLayoutParams(lp);

        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Common.BriefUser briefUser = briefUsers.get(position);

        holder.tv_username.setText(briefUser.userName);
        holder.rl_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserDetailActivity.class));
            }
        });
        imageLoader.displayImage(briefUser.userAvatar, holder.iv_avatar, displayImageOptions);
    }

    @Override
    public int getItemCount() {
        return briefUsers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout rl_content;
        private ImageView iv_avatar;
        private TextView tv_username;

        public MyViewHolder(View itemView) {
            super(itemView);

            rl_content = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }
}
