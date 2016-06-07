package com.xiang.thread;

import android.os.Message;

import com.xiang.base.BaseHandler;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

/**
 * Created by 祥祥 on 2016/5/19.
 */
public class GetTrendThread extends Thread {

    private BaseHandler mHandler;
    private int pageIndex;
    private int userId;

    public GetTrendThread(BaseHandler mHandler, int pageIndex, int userId){
        this.mHandler = mHandler;
        this.pageIndex = pageIndex;
        this.userId = userId;
    }

    @Override
    public void run() {
        super.run();

        int cmdid = 10005;
        long currentMills = System.currentTimeMillis();
        Pilot.Request10005 request = new Pilot.Request10005();
        Pilot.Request10005.Params params = new Pilot.Request10005.Params();
        Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
        request.common = common;
        request.params = params;

        params.pageIndex = pageIndex;
        params.userId = userId;
        
        byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_TRENDS, cmdid, currentMills);
        mHandler.sendDisMissProgress();
        if (null != result){
            // 加载成功
            try{
                Pilot.Response10005 response = Pilot.Response10005.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_GET_TREND_LIST_SUC;
                        msg.obj = response.data;
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

            } catch (Exception e){
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
