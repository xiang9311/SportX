package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LanchActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanch);
        ImageView iv_loading = (ImageView) findViewById(R.id.fullscreen_content);
        int ran = (int) (Math.random() * 10 % 4);
        int[] drawable = new int[]{R.mipmap.load0, R.mipmap.load1, R.mipmap.load2, R.mipmap.load3};
        iv_loading.setImageDrawable(getResources().getDrawable(drawable[ran]));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(LanchActivity.this, MainPagerActivity.class));
                finish();
            }
        }, 1000);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {

    }
}
