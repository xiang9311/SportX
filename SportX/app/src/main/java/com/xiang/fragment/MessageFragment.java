package com.xiang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiang.adapter.CommentMessageAdapter;
import com.xiang.dafault.DefaultUtil;
import com.xiang.listener.OnRclViewItemClickListener;
import com.xiang.sportx.R;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class MessageFragment extends BaseFragment {

    private View mView;

//    private TextView tv_messageCount, tv_notifyCount;
//    private RelativeLayout rl_message, rl_notify;
    private RecyclerView rv_message;

    private CommentMessageAdapter adapter;

    @Override
    protected void onInitFragment() {
        initMessage();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initMessage() {

        rv_message = (RecyclerView) findViewById(R.id.rv_message);

        adapter = new CommentMessageAdapter(getContext(), DefaultUtil.getCommentMessage(20), rv_message);
        adapter.setOnRclViewItemClickListener(new OnRclViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Snackbar.make(mView, )
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                final MaterialDialog materialDialog = new MaterialDialog(getContext());
                materialDialog.setTitle("删除消息");
                materialDialog.setMessage("删除");
                materialDialog.setPositiveButton("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(mView, "已经删除", Snackbar.LENGTH_SHORT).show();
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setNegativeButton("取消", new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setCanceledOnTouchOutside(true);
                materialDialog.show();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv_message.setAdapter(adapter);
        rv_message.setLayoutManager(manager);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_message, container, false);
        return mView;
    }

    private View findViewById(int id) {
        return mView.findViewById(id);
    }
}
