package com.xiang.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiang.Util.Constant;
import com.xiang.Util.SportTimeUtil;
import com.xiang.Util.ViewUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.GymDetailActivity;
import com.xiang.sportx.ImageAndTextActivity;
import com.xiang.sportx.R;
import com.xiang.sportx.TrendDetailActivity;
import com.xiang.sportx.UserDetailActivity;
import com.xiang.transport.TrendStatic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 祥祥 on 2016/4/25.
 */
public class TrendAdapter extends BaseRecyclerAdapter<TrendAdapter.MyViewHolder> {

    private Context context;
    private List<Common.Trend> trends = new ArrayList<>();
    private RecyclerView recyclerView;
    private int from;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions avatarOption = DisplayOptionsFactory.createBigAvatarOption(0);
    private DisplayImageOptions imageOptions = DisplayOptionsFactory.createNormalImageOption();

    public TrendAdapter(Context context, List<Common.Trend> trends, RecyclerView recyclerView, int from) {
        super(context, trends, recyclerView);
        this.context = context;
        this.trends = trends;
        this.recyclerView = recyclerView;
        this.from = from;
    }

    private OnRclViewItemClickListener onRclViewItemClickListener;
    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    /**
     *
     * @param position
     * @return 从0-9 分别代表有几张图
     */
    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);
        if(type == 0){
            return ((Common.Trend) getDataByPosition(position)).imgs.length + headMaxSize + 1;
        } else {
            return type;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = getHeadOrFootViewByType(viewType);
        if(view != null){
            return new MyViewHolder(view);
        }

        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_trend_follow, parent, false));

        int count = viewType - headMaxSize - 1;
        if (count > 1){
            holder.iv_big.setVisibility(View.GONE);
            holder.gv_images.setVisibility(View.VISIBLE);
            int i = 0;
            for(; i < count; i ++){
                holder.iv_images[i].setVisibility(View.VISIBLE);
            }

            for(; i < holder.iv_images.length; i ++){
                holder.iv_images[i].setVisibility(View.GONE);
            }

        } else if (count == 1){
            holder.iv_big.setVisibility(View.VISIBLE);
            holder.gv_images.setVisibility(View.GONE);
        } else{
            // 隐藏图片
            holder.iv_big.setVisibility(View.GONE);
            holder.gv_images.setVisibility(View.GONE);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (isHeadViewOrFootView(getItemViewType(position))){
            return ;
        }

        final Common.Trend trend = (Common.Trend) getDataByPosition(position);
        holder.tv_username.setText(trend.briefUser.userName);
        holder.tv_content.setText(trend.content);
        holder.tv_place.setText(trend.gymName);
        holder.tv_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GymDetailActivity.class);
                //TODO
                intent.putExtra(Constant.FROM, Constant.FROM_PLACE_IN_TREND);
                context.startActivity(intent);
            }
        });
        holder.tv_time.setText(SportTimeUtil.getDateFromNow(trend.createTime));

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                //TODO
                context.startActivity(intent);
            }
        });

        imageLoader.displayImage(trend.briefUser.userAvatar, holder.iv_avatar, avatarOption);

        if (trend.imgs.length > 1){
            int i = 0;
            for(; i < trend.imgs.length; i ++){
                imageLoader.displayImage(trend.imgs[i], holder.iv_images[i], imageOptions);
                final int index = i;
                holder.iv_images[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageAndTextActivity.class);
                        intent.putExtra(Constant.IMAGES, trend.imgs);
                        intent.putExtra(Constant.CURRENT_INDEX, index);
                        context.startActivity(intent);
                    }
                });
            }

        } else if (trend.imgs.length == 1){
            imageLoader.loadImage(trend.imgs[0], new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    ViewUtil.setViewHeightByWidth(context, bitmap, holder.iv_big, context.getResources().getDimension(R.dimen.follow_big_image_max_size));
                    holder.iv_big.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            holder.iv_big.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageAndTextActivity.class);
                    intent.putExtra(Constant.IMAGES, trend.imgs);
                    context.startActivity(intent);
                }
            });
        }

        holder.tv_like_count.setText(trend.likeCount + "");
        holder.tv_comment_count.setText(trend.commentCount + "");

        if(trend.isLiked){
            holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.mipmap.liked));
        } else{
            holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.mipmap.like));
        }

        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(trend.isLiked){
                    trend.likeCount = trend.likeCount - 1;
                } else{
                    trend.likeCount = trend.likeCount + 1;
                }
                trend.isLiked = !trend.isLiked;

                if(trend.isLiked){
                    holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.mipmap.liked));
                } else{
                    holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.mipmap.like));
                }
                holder.tv_like_count.setText(trend.likeCount + "");
            }
        });

        holder.rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrendStatic.setLastTrend((Common.Trend) getDataByPosition(position));

                Intent intent = new Intent(context, TrendDetailActivity.class);
                intent.putExtra(Constant.FROM, from);
                context.startActivity(intent);
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView iv_avatar;
        private TextView tv_username;
        private TextView tv_content;
        private ImageView iv_big;         // 单张 大图
        private ImageView iv_images[];  // 最多9张的小图
        private TextView tv_place;
        private TextView tv_time;

        private ImageView iv_comment, iv_like;
        private TextView tv_comment_count, tv_like_count;

        private GridLayout gv_images;
        private RelativeLayout rl_parent;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_follow_avatar);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            iv_big = (ImageView) itemView.findViewById(R.id.iv_big);
            iv_images = new ImageView[]{
                    (ImageView) itemView.findViewById(R.id.iv_image0),
                    (ImageView) itemView.findViewById(R.id.iv_image1),
                    (ImageView) itemView.findViewById(R.id.iv_image2),
                    (ImageView) itemView.findViewById(R.id.iv_image3),
                    (ImageView) itemView.findViewById(R.id.iv_image4),
                    (ImageView) itemView.findViewById(R.id.iv_image5),
                    (ImageView) itemView.findViewById(R.id.iv_image6),
                    (ImageView) itemView.findViewById(R.id.iv_image7),
                    (ImageView) itemView.findViewById(R.id.iv_image8),
            };
            tv_place = (TextView) itemView.findViewById(R.id.tv_place);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            gv_images = (GridLayout) itemView.findViewById(R.id.gv_images);

            iv_comment = (ImageView) itemView.findViewById(R.id.iv_comment);
            iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_comment_count);
            tv_like_count = (TextView) itemView.findViewById(R.id.tv_like_count);

            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
        }
    }
}
