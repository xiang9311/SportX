package com.xiang.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by 祥祥 on 2016/3/23.
 */
public class ViewUtil {

    /**
     * 根据屏幕的宽度，和bitmap的高宽，设置view的高度
     * @param bitmap
     * @param view
     * @return
     */
    public static final void setViewHeightByWindowAndBitmap(Context context, Bitmap bitmap, View view){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);  // 获取point高y宽x

        if(bitmap == null || view == null){
            return ;
        }

        int viewHeight = point.x * bitmap.getHeight() / bitmap.getWidth();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = viewHeight;
        layoutParams.width = point.x;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 根据屏幕的宽度，和bitmap的高宽，设置view的高度
     * @param context
     * @param bitmap
     * @param view
     * @param maxWidthDp dp值
     */
    public static final void setViewHeightByWidth(Context context, Bitmap bitmap, View view, float maxWidthDp){

        int maxWidth = (int) maxWidthDp;

        if (bitmap.getWidth() < maxWidth){
            maxWidth = bitmap.getWidth();
        }

        int viewHeight = maxWidth * bitmap.getHeight() / bitmap.getWidth();

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = viewHeight;
        layoutParams.width = maxWidth;

        view.setLayoutParams(layoutParams);
    }

    public static final int getWindowWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

    /**
     *
     * @param context
     * @param bitmap
     * @param view
     * @param leftMargin 左边距 dp
     * @param rightMargin 右边距 dp
     */
    public static final void setViewHeightByWindowAndBitmapAndMargin(Context context, Bitmap bitmap, View view, float leftMargin, float rightMargin){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);  // 获取point高y宽x

        if(bitmap == null || view == null){
            return ;
        }

        int viewHeight = (point.x - dp2px(context, leftMargin) - dp2px(context, rightMargin)) * bitmap.getHeight() / bitmap.getWidth();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = viewHeight;
        layoutParams.width = point.x;
        view.setLayoutParams(layoutParams);
    }

    /**
     * dp to px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dp2px(Context context, float dipValue){
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dipValue*scale+0.5f);
    }

    /**
     * px to dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int sp2dp(Context context, float spValue){
        return px2dp(context, sp2px(context, spValue));
    }


}

