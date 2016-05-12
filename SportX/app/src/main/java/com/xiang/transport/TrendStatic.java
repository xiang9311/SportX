package com.xiang.transport;

import com.xiang.proto.nano.Common;

/**
 * Created by 祥祥 on 2016/5/10.
 */
public class TrendStatic {
    private static Common.Trend trend;

    /**
     * activity跳转时 这个是对应的trend信息
     * @param trend
     */
    public static void setLastTrend(Common.Trend trend) {
        TrendStatic.trend = trend;
    }

    /**
     * activity跳转时 这个是对应的trend信息
     * @return
     */
    public static Common.Trend getLastTrend() {
        return trend;
    }
}
