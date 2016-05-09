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
import com.xiang.Util.SportTimeUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class CommentMessageAdapter extends BaseRecyclerAdapter<CommentMessageAdapter.MyViewHolder> {

    private Context context;
    private List<Common.CommentMessage> messageList;
    private RecyclerView recyclerView;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    public CommentMessageAdapter(Context context, List<Common.CommentMessage> messageList, RecyclerView recyclerView) {
        super(context, messageList, recyclerView);
        this.context = context;
        this.messageList = messageList;
        this.recyclerView = recyclerView;
    }

    private OnRclViewItemClickListener onRclViewItemClickListener = null;

    public void setOnRclViewItemClickListener(OnRclViewItemClickListener onRclViewItemClickListener) {
        this.onRclViewItemClickListener = onRclViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_comment_message, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Common.CommentMessage commentMessage = messageList.get(position);

        holder.tv_content.setText(commentMessage.messageContent);
        holder.tv_time.setText(SportTimeUtil.getDateFromNow(commentMessage.createTime));


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRclViewItemClickListener != null) {
                    onRclViewItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRclViewItemClickListener != null) {
                    onRclViewItemClickListener.OnItemLongClick(v, position);
                }
                return false;
            }
        });
        imageLoader.displayImage(commentMessage.avatar, holder.iv_avatar, options);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView iv_avatar;
        private TextView tv_content;
        private TextView tv_time;
        private RelativeLayout relativeLayout;

        public MyViewHolder(View v) {
            super(v);
            iv_avatar = (ImageView) v.findViewById(R.id.iv_avatar);
            tv_content = (TextView) v.findViewById(R.id.tv_content);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.rl_out);
        }
    }
}
