package com.xiang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.sportx.MyDetailActivity;
import com.xiang.sportx.R;
import com.xiang.sportx.UserAlbumActivity;
import com.xiang.sportx.XMoneyActivity;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class UserFragment extends BaseFragment {

    private View mView;
    private ImageView iv_avatar;
    private RelativeLayout rl_photo, rl_user_detail, rl_money;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    @Override
    protected void onInitFragment() {
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        rl_user_detail = (RelativeLayout) findViewById(R.id.rl_user_detail);
        rl_money = (RelativeLayout) findViewById(R.id.rl_money);
        initUser();


        rl_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserAlbumActivity.class));
            }
        });


        rl_user_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyDetailActivity.class));
            }
        });

        rl_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), XMoneyActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initUser() {
        imageLoader.displayImage(DefaultUtil.getBriefUser().userAvatar, iv_avatar, options);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_user, container, false);
        return mView;
    }

    private View findViewById(int id) {
        return mView.findViewById(id);
    }
}
