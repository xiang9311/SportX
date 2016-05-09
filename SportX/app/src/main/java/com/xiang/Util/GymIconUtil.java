package com.xiang.Util;

import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

/**
 * Created by 祥祥 on 2016/5/3.
 */
public class GymIconUtil {
    public static final int getIconId(int type){
        switch (type){
            case Common.PAO_BU_JI:
                return R.mipmap.runmachine;
            default:
                return R.mipmap.runmachine;
        }
    }
}
