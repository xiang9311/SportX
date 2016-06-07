package com.xiang.thread;

import android.location.Location;
import android.os.Message;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.xiang.base.BaseHandler;
import com.xiang.proto.gym.nano.Gym;
import com.xiang.proto.nano.Common;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

/**
 * Created by 祥祥 on 2016/6/6.
 */
public class GetGymListThread extends Thread {

    private Location location;
    private int pageIndex;
    private BaseHandler mHandler;

    public GetGymListThread(BaseHandler mHandler, Location location, int pageIndex){
        this.location = location;
        this.pageIndex = pageIndex;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        super.run();
        long currentMills = System.currentTimeMillis();
        int cmdid = 13001;
        Gym.Request13001 request = new Gym.Request13001();
        Gym.Request13001.Params params = new Gym.Request13001.Params();
        Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
        request.common = common;
        request.params = params;

        params.pageIndex = this.pageIndex;
        if (location != null){
            params.latitude = (float) location.getLatitude();
            params.longitude = (float) location.getLongitude();
        }

        byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_GYM_LIST, cmdid, currentMills);
        mHandler.sendDisMissProgress();
        if (null != result){
            // 加载成功
            try{
                Gym.Response13001 response = Gym.Response13001.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_GET_GYM_LIST_SUC;
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
