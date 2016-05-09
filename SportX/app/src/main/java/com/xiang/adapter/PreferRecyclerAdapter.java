package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.Constant;
import com.xiang.Util.SportTimeUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 祥祥 on 2016/4/21.
 */
public class PreferRecyclerAdapter extends RecyclerView.Adapter<PreferRecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<Common.Trend> trendList;
    private RecyclerView recyclerView;

    // tools
    private DisplayImageOptions avatarOption = DisplayOptionsFactory.createAvatarIconOption();
    private DisplayImageOptions imageOptions = DisplayOptionsFactory.createNormalImageOption();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public PreferRecyclerAdapter(Context context, List<Common.Trend> trendList, RecyclerView recyclerView) {
        this.context = context;
        this.trendList = trendList;
        this.recyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_trend_discover, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Common.Trend trend = trendList.get(position);
        imageLoader.displayImage(trend.briefUser.userAvatar, holder.circleImageView, avatarOption);
        holder.tv_name.setText(trend.briefUser.userName);
        holder.tv_timeplace.setText(SportTimeUtil.getDateFromNow(trend.createTime)
                + Constant.PONITER_CENTER
                + trend.gymName);
        try {
            imageLoader.displayImage(trend.imgs[0], holder.iv_image, imageOptions);
        } catch (NullPointerException e){
            //TODO
        } catch (IndexOutOfBoundsException e){
            // TODO
        }
        holder.tv_content.setText(trend.content);
    }

    @Override
    public int getItemCount() {
        return trendList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView circleImageView;
        private TextView tv_name;
        private TextView tv_timeplace;
        private ImageView iv_image;
        private TextView tv_content;

        public MyViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.civ_avatar_prefer);
            tv_name = (TextView) itemView.findViewById(R.id.tv_username_prefer);
            tv_timeplace = (TextView) itemView.findViewById(R.id.tv_timeplace_prefer);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
