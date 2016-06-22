package com.xiang.thread;

import android.location.Location;
import android.os.Message;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.xiang.base.BaseHandler;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

/**
 * Created by 祥祥 on 2016/5/25.
 *
 */
public class GetRecommendUserThread extends  Thread{
    private BaseHandler mHandler;
    private Location location;
    public GetRecommendUserThread(BaseHandler mHandler, Location location){
        this.mHandler = mHandler;
        this.location = location;
    }

    @Override
    public void run() {
        super.run();
        long currentMills = System.currentTimeMillis();
        int cmdid = 10019;
        Pilot.Request10019 request = new Pilot.Request10019();
        Pilot.Request10019.Params params = new Pilot.Request10019.Params();
        Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
        request.common = common;
        request.params = params;

        if (location != null){
            params.latitude = (float) location.getLatitude();
            params.longitude = (float) location.getLongitude();
        }

        byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_RECOMMEND_USER, cmdid, currentMills);
        if (null != result){
            // 加载成功
            try{
                Pilot.Response10019 response = Pilot.Response10019.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        if (response.data != null) {
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_GET_RECOMMENDUSER_SUC;
                            msg.obj = response.data.searchedUser;
                            mHandler.sendMessage(msg);
                        } else{
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_GET_RECOMMENDUSER_SUC;
                            msg.obj = new Common.SearchedUser[0];
                            mHandler.sendMessage(msg);
                        }
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
