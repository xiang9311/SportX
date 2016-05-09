package com.xiang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.xiang.sportx.R;


/**
 * Created by 祥祥 on 2016/3/4.
 */
public class WXHorizontalProgressBar extends View {

    private Paint paint;

    private int progress = 0;
    private int mProgress = 0;
    private int startProgress = 0;

    private int totleTime = 300;
    private int interval = 10;
    private int usedTime = 0;

    private MyThread myThread;

    public WXHorizontalProgressBar(Context context) {
        super(context);
        init(null);
    }

    public WXHorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WXHorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        paint = new Paint();
        myThread = new MyThread();

        if (attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.WXHorizontalProgressBar, 0, 0);
            int color = array.getColor(R.styleable.WXHorizontalProgressBar_paintColor, 0xFF69B4);
            array.recycle();
            paint.setColor(color);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, this.getWidth() * mProgress / 100, this.getHeight(), paint);
        if (mProgress >= 100){
            if (getVisibility() == View.VISIBLE){
                startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
            }
            setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置进度条颜色
     * @param color
     */
    public void setColor(int color){
        paint.setColor(color);
        invalidate();
    }

    public void setProgress (int progress){
        this.progress = progress;
        if (mProgress > progress){
            mProgress = 0;
        }
        startProgress = mProgress;
        usedTime = 0;

        if (!myThread.isAlive()){
            myThread = new MyThread();
            myThread.start();
        }

    }

    class MyThread extends Thread {
        @Override
        public void run() {
            while(true){
                mProgress += (progress - startProgress) / (totleTime/interval);
                try {
                    sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postInvalidate();

                if (mProgress >= progress){
                    break;
                }
            }
        }
    }

}
