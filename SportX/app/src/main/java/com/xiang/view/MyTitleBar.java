package com.xiang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiang.sportx.R;


/**
 * Created by 祥祥 on 2016/3/8.
 */
public class MyTitleBar extends RelativeLayout {

    private Context context = null;

    private ImageView iv_back, iv_more;
    private TextView tv_title;
    private RelativeLayout rl_root;

    private View root;

    public MyTitleBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();

    }

    public MyTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 初始化view
     */
    private void init(){
        root = LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, true);
        iv_back = (ImageView) root.findViewById(R.id.iv_back);
        iv_more = (ImageView) root.findViewById(R.id.iv_more);
        tv_title = (TextView) root.findViewById(R.id.tv_header);
        rl_root = (RelativeLayout) root.findViewById(R.id.rv_root);
    }

    /**
     *
     * @param resId 资源id
     */
    public void setTitleBackgroundColor(int resId){
        rl_root.setBackgroundResource(resId);
    }

    /**
     * 设置标题内容
     * @param title
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    /**
     * 设置返回按钮
     * @param resId 显示的图片的资源id
     * @param show 是否显示
     * @param listener 点击事件
     */
    public void setBackButton(int resId, boolean show, OnClickListener listener){
        if (show){
            iv_back.setVisibility(View.VISIBLE);
            if (0 != resId){
                iv_back.setImageResource(resId);
            }
            if (null != listener){
                iv_back.setOnClickListener(listener);
            }
        } else{
            iv_back.setVisibility(View.GONE);
        }
    }

    public void setMoreButton(int resId, boolean show, OnClickListener listener){
        if (show){
            iv_more.setVisibility(View.VISIBLE);
            if (0 != resId){
                iv_more.setImageResource(resId);
            }
            if (null != listener){
                iv_more.setOnClickListener(listener);
            }
        } else{
            iv_more.setVisibility(View.GONE);
        }
    }
}