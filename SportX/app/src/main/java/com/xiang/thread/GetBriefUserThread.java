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
 * 此线程是后台默默获取聊天用户简要信息的线程，只有获取成功时通知更新cache，否则不做任何处理
 */
public class GetBriefUserThread extends  Thread{
    private BaseHandler mHandler;
    private int userId;
    public GetBriefUserThread(BaseHandler mHandler, int userId){
        this.mHandler = mHandler;
        this.userId = userId;
    }

    @Override
    public void run() {
        super.run();
        long currentMills = System.currentTimeMillis();
        int cmdid = 10018;
        Pilot.Request10018 request = new Pilot.Request10018();
        Pilot.Request10018.Params params = new Pilot.Request10018.Params();
        Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
        request.common = common;
        request.params = params;

        params.userId = userId;

        byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_GET_BRIEF_USER, cmdid, currentMills);
        if (null != result){
            // 加载成功
            try{
                Pilot.Response10018 response = Pilot.Response10018.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_GET_BRIEFUSER_SUC;
                        msg.obj = response.data.briefUser;
                        mHandler.sendMessage(msg);
                    } else{
                        // code is not 0, find error
//                        Message msg = Message.obtain();
//                        msg.what = BaseHandler.KEY_ERROR;
//                        msg.obj = response.common.message;
//                        mHandler.sendMessage(msg);
                    }
                } else {
//                    Message msg = Message.obtain();
//                    msg.what = BaseHandler.KEY_ERROR;
//                    msg.obj = "数据错误";
//                    mHandler.sendMessage(msg);
                }

            } catch (InvalidProtocolBufferNanoException e){
//                Message msg = Message.obtain();
//                msg.what = BaseHandler.KEY_PARSE_ERROR;
//                mHandler.sendMessage(msg);
//                e.printStackTrace();
            }
        } else {
            // 加载失败
//            Message msg = Message.obtain();
//            msg.what = BaseHandler.KEY_NO_RES;
//            mHandler.sendMessage(msg);
        }
    }
}
