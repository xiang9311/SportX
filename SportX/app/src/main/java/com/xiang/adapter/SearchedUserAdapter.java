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
import com.xiang.Util.ImageUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/17.
 */
public class SearchedUserAdapter extends RecyclerView.Adapter<SearchedUserAdapter.MyViewHolder> {

    private Context context;
    private List<Common.SearchedUser> searchedUsers;
    private RecyclerView recyclerView;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions = DisplayOptionsFactory.createNormalImageOption();

    public SearchedUserAdapter(Context context, List<Common.SearchedUser> searchedUsers, RecyclerView recyclerView) {
        this.context = context;
        this.searchedUsers = searchedUsers;
        this.recyclerView = recyclerView;
    }

    private OnRclViewItemClickListener onRclViewItemClickListener;

    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return searchedUsers.get(position).images.length;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SearchedUserAdapter.MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_user_in_search, parent, false));

        int i = 0;
        for(; i < viewType && i < viewHolder.imageViews.length; i ++){
            viewHolder.imageViews[i].setVisibility(View.VISIBLE);
        }

        for(; i < viewHolder.imageViews.length; i ++){  // 3是imageView的数量
            viewHolder.imageViews[i].setVisibility((viewType > 0) ? View.INVISIBLE : View.GONE);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Common.SearchedUser searchedUser = searchedUsers.get(position);

        holder.tv_sign.setText(searchedUser.sign);
        holder.tv_username.setText(searchedUser.userName);
        imageLoader.displayImage(searchedUser.userAvatar, holder.iv_avatar, displayImageOptions);
        holder.rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRclViewItemClickListener != null){
                    onRclViewItemClickListener.onItemClick(v, position);
                }
            }
        });

        for(int i = 0; i < searchedUser.images.length && i < holder.imageViews.length; i ++){
            imageLoader.displayImage(ImageUtil.getSearchedImageUrlSmall(searchedUser.images[i]), holder.imageViews[i], displayImageOptions);
        }
    }

    @Override
    public int getItemCount() {
        return searchedUsers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout rl_parent;
        public ImageView iv_avatar;
        public TextView tv_username, tv_sign;
        public ImageView imageViews[] = new ImageView[3];

        public MyViewHolder(View itemView) {
            super(itemView);

            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_sign = (TextView) itemView.findViewById(R.id.tv_sign);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);

            imageViews[0] = (ImageView) itemView.findViewById(R.id.iv_image0);
            imageViews[1] = (ImageView) itemView.findViewById(R.id.iv_image1);
            imageViews[2] = (ImageView) itemView.findViewById(R.id.iv_image2);
        }
    }
}
