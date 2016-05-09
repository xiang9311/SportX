package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 直接设置启动activity，跳过此步骤。（肯定可以的）
        startActivity(new Intent(MainActivity.this, LanchActivity.class));
        finish();
    }
}
