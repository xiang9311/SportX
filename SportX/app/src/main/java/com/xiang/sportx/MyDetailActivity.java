package com.xiang.sportx;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xiang.Util.Constant;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.proto.nano.Common;
import com.xiang.view.MyTitleBar;
import com.xiang.view.TwoOptionMaterialDialog;

import me.drakeet.materialdialog.MaterialDialog;

public class MyDetailActivity extends BaseAppCompatActivity {

    private MyTitleBar myTitleBar;
    private ImageView iv_avatar;
    private RelativeLayout rl_avatar, rl_name, rl_sex, rl_sign;
    private View v_username, v_sign;
    private MaterialEditText met_username, met_sign;

    //dialog
    private MaterialDialog md_username, md_sign;
    private TwoOptionMaterialDialog md_changeavatar, md_sex;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    private Common.BriefUser briefUser = DefaultUtil.getBriefUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_my_detail);
        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        rl_avatar = (RelativeLayout) findViewById(R.id.rl_avatar);
        rl_name = (RelativeLayout) findViewById(R.id.rl_username);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_sign = (RelativeLayout) findViewById(R.id.rl_sign);


        //init dialog views
        v_username = LayoutInflater.from(this).inflate(R.layout.md_input, null, false);
        met_username = (MaterialEditText) v_username.findViewById(R.id.met_input);
        met_username.setMaxCharacters(Constant.MAX_LENGTH_USER_NAME);
        met_username.setFloatingLabelText("昵称");
        met_username.setHint("昵称");

        v_sign = LayoutInflater.from(this).inflate(R.layout.md_input, null, false);
        met_sign = (MaterialEditText) v_sign.findViewById(R.id.met_input);
        met_sign.setMaxCharacters(Constant.MAX_LENGTH_SIGN);
        met_sign.setFloatingLabelText("个性签名");
        met_sign.setHint("个性签名");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myTitleBar.setTitle("个人信息");
        myTitleBar.setMoreButton(0, false, null);

        imageLoader.displayImage(briefUser.userAvatar, iv_avatar, options);

        // md  点击事件
        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(md_username == null){
                    md_username = new MaterialDialog(MyDetailActivity.this);
                    md_username.setContentView(v_username);
                    md_username.setCanceledOnTouchOutside(true);
                    md_username.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //
                            Snackbar.make(rl_avatar,""+met_username.getText().toString(), Snackbar.LENGTH_SHORT).show();
                            md_username.dismiss();
                            met_username.setText("");
                        }
                    });
                }
                md_username.show();
            }
        });

        rl_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(md_sign == null){
                    md_sign = new MaterialDialog(MyDetailActivity.this);
                    md_sign.setContentView(v_sign);
                    md_sign.setCanceledOnTouchOutside(true);
                    md_sign.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //
                            Snackbar.make(rl_avatar,""+met_sign.getText().toString(), Snackbar.LENGTH_SHORT).show();
                            md_sign.dismiss();
                            met_sign.setText("");
                        }
                    });
                }
                md_sign.show();
            }
        });

        rl_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = new String[]{"相册", "拍照"};
                if(md_changeavatar == null){
                    md_changeavatar = MaterialDialogFactory.createTwoOptionMd(MyDetailActivity.this, options, false, 0);
                    md_changeavatar.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                        @Override
                        public void onOptionChoose(int index) {
                            md_changeavatar.dismiss();
                            Snackbar.make(rl_avatar, "" + index, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    md_changeavatar.setTitle("选择方式");
                }
                md_changeavatar.show();
            }
        });

        rl_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = new String[]{"男", "女"};
                if(md_sex == null){
                    md_sex = MaterialDialogFactory.createTwoOptionMd(MyDetailActivity.this, options, true, 0);
                    md_sex.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                        @Override
                        public void onOptionChoose(final int index) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    md_sex.dismiss();
                                    Snackbar.make(rl_avatar, "" + options[index], Snackbar.LENGTH_SHORT).show();
                                }
                            }, 250);  // 这个时间时redio的动画时间
                        }
                    });
                    md_sex.setTitle("选择性别");
                }
                md_sex.show();
            }
        });
    }
}
