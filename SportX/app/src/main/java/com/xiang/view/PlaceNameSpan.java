package com.xiang.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.xiang.sportx.R;

/**
 * Created by 祥祥 on 2016/5/9.
 */
public class PlaceNameSpan extends ClickableSpan {
    View.OnClickListener onClickListener;
    Context context;
    public PlaceNameSpan(Context context, View.OnClickListener onClickListener){
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(context.getResources().getColor(R.color.text_high_light));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        onClickListener.onClick(widget);
    }
}