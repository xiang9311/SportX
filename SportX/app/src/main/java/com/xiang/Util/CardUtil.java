package com.xiang.Util;

import com.xiang.proto.nano.Common;

/**
 * Created by 祥祥 on 2016/4/28.
 */
public class CardUtil {
    public static final String getCardName(int cardType){
        switch (cardType){
            case Common.Once:
                return "体验卡";
            case Common.Month:
                return "月卡";
            case Common.Quarter:
                return "季度卡";
            default:
                return "类型错误";
        }
    }
}
