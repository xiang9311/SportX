package com.xiang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.dafault.DefaultUtil;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.sportx.R;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class UserFragment extends BaseFragment {

    private View mView;
    private ImageView iv_avatar;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();

    @Override
    protected void onInitFragment() {
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);

        initUser();
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
