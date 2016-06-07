package com.xiang.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
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
     * 根据屏幕宽度和边距 计算出剩余图片可以显示的大小
     *
     * @param context
     * @param bitmap
     * @param view
     * @param usedSize
     */
    public static final void setViewSizeByUsedWidth(Context context, Bitmap bitmap, View view, float usedSize){

        int windowWidth = getWindowWidth(context);
        int lastWidth = (int) (windowWidth - usedSize);

        float viewWidth = lastWidth * 0.9f;

        if (bitmap.getWidth() < viewWidth){
            viewWidth = bitmap.getWidth();
        }

        float viewHeight = viewWidth * bitmap.getHeight() / bitmap.getWidth();

        if(viewHeight > lastWidth){        // 如果viewheight过高，则限制高度为lastWidth， 并重新计算kuandu
            viewHeight = lastWidth;
            viewWidth = viewHeight * bitmap.getWidth() / bitmap.getHeight();
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) viewHeight;
        layoutParams.width = (int) viewWidth;

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
     * @param context
     * @param pxValue
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

