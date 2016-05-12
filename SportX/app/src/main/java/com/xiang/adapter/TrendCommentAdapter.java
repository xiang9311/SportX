package com.xiang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.GymDetailActivity;
import com.xiang.sportx.R;
import com.xiang.sportx.UserDetailActivity;
import com.xiang.view.PlaceNameSpan;
import com.xiang.view.UserNameSpan;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/9.
 */
public class TrendCommentAdapter extends BaseRecyclerAdapter<TrendCommentAdapter.MyViewHolder> {

    private List<Common.Comment> data;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions = DisplayOptionsFactory.createNormalImageOption();
    private DisplayImageOptions avatarOptions = DisplayOptionsFactory.createAvatarIconOption();

    public TrendCommentAdapter(Context context, List<Common.Comment> data, RecyclerView recyclerView) {
        super(context, data, recyclerView);
        this.data = data;
    }

    /**
     * item点击事件
     */
    private OnRclViewItemClickListener onRclViewItemClickListener;
    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    @Override
    public TrendCommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeadViewOrFootView(viewType)){
            return new MyViewHolder(getHeadOrFootViewByType(viewType));
        } else{
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_trend_comment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (isHeadViewOrFootView(getItemViewType(position))){
            return ;
        }

        Common.Comment comment = (Common.Comment) getDataByPosition(position);

        imageLoader.displayImage(comment.briefUser.userAvatar, holder.iv_avatar, avatarOptions);
        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                //TODO
                context.startActivity(intent);
            }
        });

        holder.tv_username.setText(comment.briefUser.userName);

        setCommentText(holder.tv_content, comment);

        setPlaceTime(holder.tv_place_time, comment);
    }

    /**
     * 设置时间+地址
     * @param tv_place_time
     * @param comment
     */
    private void setPlaceTime(TextView tv_place_time, final Common.Comment comment) {
        tv_place_time.setText("");
        if(!comment.gymName.equals("")) {
            SpannableString span = new SpannableString(comment.gymName);
            PlaceNameSpan placeNameSpan = new PlaceNameSpan(context, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Snackbar.make(recyclerView, comment.gymName, Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, GymDetailActivity.class);
                    //TODO
                    intent.putExtra(Constant.FROM, Constant.FROM_PLACE_IN_TREND);
                    context.startActivity(intent);
                }
            });
            span.setSpan(placeNameSpan, 0, comment.gymName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_place_time.append(span);
            tv_place_time.append(" ");
            tv_place_time.append(Constant.PONITER_CENTER);
            tv_place_time.append(" ");
        }
        tv_place_time.append(SportTimeUtil.getDateFromNow(comment.createTime));
        tv_place_time.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 设置评论的内容
     * @param tv_content
     * @param comment
     */
    private void setCommentText(TextView tv_content, final Common.Comment comment) {
        tv_content.setText("");
        if (comment.toUserid != 0 && comment.toUserName != null && !comment.toUserName.equals("")){
            //
            tv_content.append("回复：");
            SpannableString span = new SpannableString(comment.toUserName);
            UserNameSpan userNameSpan = new UserNameSpan(context, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Snackbar.make(recyclerView, comment.toUserName, Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    //TODO
                    context.startActivity(intent);
                }
            });
            span.setSpan(userNameSpan, 0, comment.toUserName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_content.append(span);
            tv_content.append(" ");
            tv_content.append(comment.commentContent);
        } else{
            tv_content.setText(comment.commentContent);
        }
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView iv_avatar;
        public TextView tv_username;
        public TextView tv_content;
        public TextView tv_place_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_place_time = (TextView) itemView.findViewById(R.id.tv_place_time);
        }
    }
}
