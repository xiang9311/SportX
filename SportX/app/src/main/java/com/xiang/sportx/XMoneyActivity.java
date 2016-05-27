package com.xiang.sportx;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.xiang.Util.Constant;
import com.xiang.base.BaseHandler;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.view.MateriaDialogWidthCheckBox;
import com.xiang.view.MyTitleBar;

import me.drakeet.materialdialog.MaterialDialog;

public class XMoneyActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RelativeLayout rl_card, rl_shop, rl_money;

    private MaterialDialog md_no_card, md_no_shop;
    private MateriaDialogWidthCheckBox md_money;
    private TextView tv_money;

    // tools
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_xmoney);
        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        rl_card = (RelativeLayout) findViewById(R.id.rl_card);
        rl_shop = (RelativeLayout) findViewById(R.id.rl_shop);
        rl_money = (RelativeLayout) findViewById(R.id.rl_money);
        tv_money = (TextView) findViewById(R.id.tv_money);
    }

    @Override
    protected void initData() {
        sp = getSharedPreferences(Constant.SP_DATA, MODE_PRIVATE);
    }

    @Override
    protected void configView() {
        myTitleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myTitleBar.setTitle("我的X币");
        myTitleBar.setMoreButton(0, false, null);

        rl_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (md_no_card == null) {
                    md_no_card = new MaterialDialog(XMoneyActivity.this);
                    md_no_card.setTitle("通知");
                    md_no_card.setMessage("X币换卡活动最迟将在7月1日开启，敬请期待。");
                    md_no_card.setPositiveButton("我知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            md_no_card.dismiss();
                        }
                    });
                    md_no_card.setCanceledOnTouchOutside(true);
                }
                md_no_card.show();
            }
        });

        rl_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (md_no_shop == null) {
                    md_no_shop = new MaterialDialog(XMoneyActivity.this);
                    md_no_shop.setTitle("通知");
                    md_no_shop.setMessage("设备商城最迟将在7月15日开启，敬请期待。");
                    md_no_shop.setPositiveButton("我知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            md_no_shop.dismiss();
                        }
                    });
                    md_no_shop.setCanceledOnTouchOutside(true);
                }
                md_no_shop.show();
            }
        });

        rl_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMdMoney();
            }
        });

        // 是否自动显示X币使用须知
        if (sp.getBoolean(Constant.SP_AUTO_SHOW_MONEY_DIALOG, true)){
            showMdMoney();
        }

        mHandler = new MyHandler(this, null);

        new GetMyMoneyThread().start();
    }

    private void showMdMoney(){
        if(md_money == null){
            md_money = MaterialDialogFactory.createCheckBoxMd(XMoneyActivity.this, getResources().getString(R.string.money_info), "不再提示", !sp.getBoolean(Constant.SP_AUTO_SHOW_MONEY_DIALOG, true));
            md_money.setPositiveButton("我知道了", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    md_money.dismiss();
                }
            });
            md_money.setOnCheckListener(new MateriaDialogWidthCheckBox.OnCheckListener() {
                @Override
                public void onCheck(boolean checked) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constant.SP_AUTO_SHOW_MONEY_DIALOG, !checked);
                    editor.commit();
                }
            });
            md_money.setTitle("X币使用须知");
            md_money.setCanceledOnTouchOutside(true);
        }
        md_money.show();
    }


    private MyHandler mHandler;
    private final int KEY_GET_MONEY_SUC = 101;

    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_MONEY_SUC:
                    int money = msg.arg1;
                    tv_money.setText(""+money);
                    break;
            }
        }
    }

    class GetMyMoneyThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10008;
            Pilot.Request10008 request = new Pilot.Request10008();
            Pilot.Request10008.Params params = new Pilot.Request10008.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;


            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_MY_XMONEY, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10008 response = Pilot.Response10008.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_GET_MONEY_SUC;
                            msg.arg1 = response.data.count;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }
}
