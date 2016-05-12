package com.xiang.sportx;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiang.Util.Constant;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.view.MateriaDialogWidthCheckBox;
import com.xiang.view.MyTitleBar;

import me.drakeet.materialdialog.MaterialDialog;

public class XMoneyActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private RelativeLayout rl_card, rl_shop, rl_money;

    private MaterialDialog md_no_card, md_no_shop;
    private MateriaDialogWidthCheckBox md_money;

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
}
