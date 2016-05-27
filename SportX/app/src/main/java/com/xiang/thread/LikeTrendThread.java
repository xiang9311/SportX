package com.xiang.thread;

import android.os.Message;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.xiang.base.BaseHandler;
import com.xiang.proto.nano.Common;
import com.xiang.proto.trend.nano.Trend;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

/**
 * Created by 祥祥 on 2016/5/24.
 */
public class LikeTrendThread extends Thread{

    private int trendId;
    private boolean isLike;
    private BaseHandler mHandler;
    private int dataIndex;

    public LikeTrendThread(BaseHandler baseHandler, int trendId, boolean isLike, int dataIndex){
        this.mHandler = baseHandler;
        this.trendId = trendId;
        this.isLike = isLike;
        this.dataIndex = dataIndex;
    }

    @Override
    public void run() {
        super.run();
        long currentMills = System.currentTimeMillis();
        int cmdid = 12005;
        Trend.Request12005 request = new Trend.Request12005();
        Trend.Request12005.Params params = new Trend.Request12005.Params();
        Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
        request.common = common;
        request.params = params;

        params.likeTrend = isLike;
        params.trendId = trendId;

        byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_LIKE_TREND, cmdid, currentMills);
        if (null != result){
            // 加载成功
            try{
                Trend.Response12005 response = Trend.Response12005.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        Message msg = Message.obtain();
                        msg.arg1 = dataIndex;
                        msg.what = isLike ? BaseHandler.KEY_LIKE_TREND : BaseHandler.KEY_UNLIKE_TREND;
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
