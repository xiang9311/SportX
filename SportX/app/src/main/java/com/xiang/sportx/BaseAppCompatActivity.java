package com.xiang.sportx;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xiang.Util.Constant;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 祥祥 on 2016/3/8.
 * 继承该类的activity必须把setContentView方法的调用放到initView的首行！！！
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    /**
     * ！！！ 如果在 configview之前 设置该字段为true，则不会自动执行configView ！！！
     */
    protected boolean configViewLater = false;

    private MaterialDialog md_progress;
    private TextView tv_progross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initView();
        this.initData();
        if ( !configViewLater) {
            this.configView();
        }
    }

    private List<Bitmap> bitmaps = new ArrayList<>();

    protected abstract void initView();
    protected abstract void initData();
    protected abstract void configView();

    /**
     * Constant.DEBUG 时才会输出log
     * @param key
     * @param content
     */
    public void logD(String key, String content){
        if (Constant.DEBUG){
            Log.d(key, content);
        }
    }

    /**
     * 发送toast通知
     * @param content
     */
    public void sendToast(String content){
//        Toast.makeText(getBaseContext(), content, Toast.LENGTH_SHORT).show();
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), content, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void addBitmapToRecycle(Bitmap bitmap){
        bitmaps.add(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i = 0; i < bitmaps.size(); i ++){
            Bitmap bitmap = bitmaps.get(i);
            if (bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
    }

    /**
     *
     * @param content
     * @param canceledOnTouchOutside
     */
    public void showProgress(String content, boolean canceledOnTouchOutside){
        if (md_progress == null){
            md_progress = new MaterialDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.view_progress, null);
            tv_progross = (TextView) view.findViewById(R.id.tv_progress_content);
            md_progress.setContentView(view);
        }

        md_progress.setCanceledOnTouchOutside(canceledOnTouchOutside);
        if (content == null || content.equals("")){
            tv_progross.setVisibility(View.GONE);
        } else{
            tv_progross.setText(content);
            tv_progross.setVisibility(View.VISIBLE);
        }

        md_progress.show();
    }

    public void dissmissProgress(){
        if(md_progress != null){
            md_progress.dismiss();
        }
    }
}
