package com.xiang.database.model;

import java.io.Serializable;

/**
 * Created by 祥祥 on 2016/6/6.
 */
public class TblHistoryKeyword implements Serializable {
    public int id;
    public String keyword;
    public int order;

    public TblHistoryKeyword(int id, String keyword, int order) {
        this.id = id;
        this.keyword = keyword;
        this.order = order;
    }
}
