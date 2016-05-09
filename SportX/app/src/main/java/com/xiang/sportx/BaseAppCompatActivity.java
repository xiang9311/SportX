package com.xiang.sportx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.xiang.Util.Constant;

/**
 * Created by 祥祥 on 2016/3/8.
 * 继承该类的activity必须把setContentView方法的调用放到initView的首行！！！
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initView();
        this.initData();
        this.configView();
    }

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
        Toast.makeText(getBaseContext(), content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送toast通知
     * @param resId
     */
    public void sendToast(int resId){
        Toast.makeText(getBaseContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
