package com.xiang.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by 祥祥 on 2016/5/11.
 */
public class MyRecyclerView extends RecyclerView {

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public MyRecyclerView(Context context) {
        super(context);
        init(null);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state){
            case SCROLL_STATE_IDLE:
                imageLoader.resume();
                break;
            case SCROLL_STATE_DRAGGING:
                imageLoader.pause();
                break;
            case SCROLL_STATE_SETTLING:
                break;
        }
    }
}
