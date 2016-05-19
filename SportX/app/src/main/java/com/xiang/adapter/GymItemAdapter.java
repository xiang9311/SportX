package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;
import com.xiang.Util.GymIconUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class GymItemAdapter extends BaseRecyclerAdapter<GymItemAdapter.MyViewHolder> {

    private Context context;
    private List<Common.BriefGym> gyms;
    private RecyclerView recyclerView;

    public GymItemAdapter(Context context, List<Common.BriefGym> gyms, RecyclerView recyclerView) {
        super(context, gyms, recyclerView);
        this.context = context;
        this.gyms = gyms;
        this.recyclerView = recyclerView;
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
        Log.d("onCreateViewHolder", "onCreateViewHolder" +viewType);
        View view = getHeadOrFootViewByType(viewType);

        if(view != null){
            return new MyViewHolder(view);
        } else {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listview_gym, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Log.d("onBindViewHolder", "onBindViewHolder" +position);
        if(isHeadViewOrFootView(getItemViewType(position))){
            return ;
        }

        Common.BriefGym briefGym = (Common.BriefGym) getDataByPosition(position);

        holder.tv_gym_name.setText(briefGym.gymName);
        holder.tv_place.setText(briefGym.place);

        holder.ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRclViewItemClickListener != null){
                    onRclViewItemClickListener.onItemClick(v, position);
                }
            }
        });

        holder.flowLayout.removeAllViews();
        for (int i = 0; i < briefGym.equipments.length; i ++){
            View iconView = LayoutInflater.from(context).inflate(R.layout.view_equment_icon, null);
            ImageView imageView = (ImageView) iconView.findViewById(R.id.iv_icon);
            imageView.setImageDrawable(context.getResources().getDrawable(GymIconUtil.getIconId(briefGym.equipments[i].equipmentType)));
            int size = (int) context.getResources().getDimension(R.dimen.gym_equipment_icon);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(size, size);
            if(i != 0){
                layoutParams.leftMargin = 5;
            }
            holder.flowLayout.addView(iconView, layoutParams);
        }

        for (int i = 0; i < holder.imageViews.length && i < briefGym.gymCover.length; i ++){
            imageLoader.displayImage(briefGym.gymCover[i], holder.imageViews[i]);
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_gym_name;
        public ImageView imageViews[], imageView_0, imageView_1, imageView_2;
        public FlowLayout flowLayout;
        public TextView tv_place;
        public LinearLayout ll_parent;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_gym_name = (TextView) itemView.findViewById(R.id.tv_gym_name);
            flowLayout = (FlowLayout) itemView.findViewById(R.id.flowLayout);
            tv_place = (TextView) itemView.findViewById(R.id.tv_place);

            imageView_0 = (ImageView) itemView.findViewById(R.id.iv_gym_image0);
            imageView_1 = (ImageView) itemView.findViewById(R.id.iv_gym_image1);
            imageView_2 = (ImageView) itemView.findViewById(R.id.iv_gym_image2);

            imageViews = new ImageView[]{imageView_0, imageView_1, imageView_2};

            ll_parent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        }
    }
}
