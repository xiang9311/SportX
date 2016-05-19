package com.xiang.sportx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xiang.view.MyTitleBar;

public class LoginActivity extends BaseAppCompatActivity {

    private MyTitleBar titleBar;
    private MaterialEditText met_phone, met_password;
    private ActionProcessButton apb_login, apb_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        met_phone = (MaterialEditText) findViewById(R.id.met_phone);
        met_password = (MaterialEditText) findViewById(R.id.met_password);
        apb_login = (ActionProcessButton) findViewById(R.id.apb_login);
        apb_register = (ActionProcessButton) findViewById(R.id.apb_register);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        titleBar.setDefault("登录");
        apb_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        apb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO login
            }
        });
    }
}
