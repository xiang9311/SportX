package com.xiang.database.model;

import com.xiang.Util.UserStatic;

import java.io.Serializable;

/**
 * Created by 祥祥 on 2016/5/20.
 */
public class TblBriefUser implements Serializable {
    public String id;
    public String userName;
    public String userAvatar;

    public TblBriefUser(String id, String userName, String userAvatar) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    public static TblBriefUser createStaticBriefUser(){
        return new TblBriefUser(UserStatic.userId+"", UserStatic.realUserName, UserStatic.avatarUrl);
    }
}
