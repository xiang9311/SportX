package com.xiang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 祥祥 on 2016/5/4.
 */
public abstract class BaseFragment extends Fragment {

    private boolean created = false;
    private boolean rootViewCreated = false;

    protected abstract void onInitFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootViewCreated = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(!rootViewCreated){
            return ;
        }

        if(isVisibleToUser){
            if (created){
                //
            } else{
                Log.d("setUserVisibleHint", "start");
                onInitFragment();
                created = true;
                Log.d("setUserVisibleHint", "inited");
            }
        } else{
            //
        }
    }
}
