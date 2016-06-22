package com.xiang.thread;

import android.os.Message;

import com.xiang.base.BaseHandler;
import com.xiang.handler.GetRongyunHandler;
import com.xiang.proto.pilot.nano.Token;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;

/**
 * Created by 祥祥 on 2016/5/19.
 */
public class GetRongyunTokenThread extends Thread {

    private GetRongyunHandler mHandler;
    private Token.Request11002 request11002;

    public GetRongyunTokenThread(GetRongyunHandler baseHandler, Token.Request11002 request11002){
        this.mHandler = baseHandler;
        this.request11002 = request11002;
    }

    @Override
    public void run() {
        super.run();

        int cmdid = 11002;
        long currentMills = System.currentTimeMillis();
        byte[] result = RequestUtil.postWithProtobuf(request11002, UrlUtil.URL_GET_RONGYUN_TOKEN, cmdid, currentMills);
        if (null != result){
            // 加载成功
            try{
                Token.Response11002 response = Token.Response11002.parseFrom(result);

                if (response.common != null){
                    if(response.common.code == 0){
                        Message msg = Message.obtain();
                        msg.what = GetRongyunHandler.KEY_GET_RONGYUN_TOKEN_SUC;
                        msg.obj = response.data.rongyunToken;
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
