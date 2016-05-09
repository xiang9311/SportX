package com.xiang.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 祥祥 on 2016/3/21.
 */
public class ArrayUtil {
    /**
     * 数组转list
     * @param array
     * @param <T>
     * @return
     */
    public static <T> List<T> Array2List(T[] array){
        List<T> list = new ArrayList<T>();
        for (T t: array) {
            list.add(t);
        }
        return list;
    }

}
