package com.xiang.thread;

import android.os.Message;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.xiang.base.BaseHandler;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

/**
 * Created by 祥祥 on 2016/5/25.
 */
public class GetMyFensiThread extends  Thread{
    private BaseHandler mHandler;
    private int userId;
    public GetMyFensiThread(BaseHandler mHandler, int userId){
        this.mHandler = mHandler;
        this.userId = userId;
    }

    @Override
    public void run() {
        super.run();
        long currentMills = System.currentTimeMillis();
        int cmdid = 10010;
        Pilot.Request10010 request = new Pilot.Request10010();
        Pilot.Request10010.Params params = new Pilot.Request10010.Params();
        Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
        request.common = common;
        request.params = params;

        params.userId = userId;

        byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_USER_FENSI, cmdid, currentMills);
        mHandler.sendDisMissProgress();
        if (null != result){
            // 加载成功
            try{
                Pilot.Response10010 response = Pilot.Response10010.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_GET_FENSI_SUC;
                        try{
                            msg.obj = response.data.briefUsers;
                        } catch (NullPointerException e){
                            msg.obj = new Common.BriefUser[0];
                        }
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
