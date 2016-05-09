package com.xiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/4/29.
 */
public class SwipeStackAdapter extends BaseAdapter {

    private Context context;
    private List<Common.BriefUser> briefUsers;

    public SwipeStackAdapter(Context context, List<Common.BriefUser> briefUsers) {
        this.context = context;
        this.briefUsers = briefUsers;
    }

    @Override
    public int getCount() {
        return briefUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return briefUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.listitem_user_stack, parent, false);

        return convertView;
    }
}
