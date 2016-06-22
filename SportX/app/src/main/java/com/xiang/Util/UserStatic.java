package com.xiang.Util;

import com.xiang.proto.nano.Common;

/**
 * Created by 祥祥 on 2016/3/22.
 */
public class UserStatic {
    /**
     * 头像资源id
     */
    public static int avatarResId = 0;
    /**
     * 头像url
     */
    public static String avatarUrl = "";
    /**
     * 登录时使用的用户名，也可能是手机号
     */
    public static String loginUserName = "";
    /**
     * 密码
     */
    public static String psssword = "";
    /**
     * 用户手机号
     */
    public static String phone = "";
    /**
     * 真实的用户名
     */
    public static String realUserName = "";
    /**
     * 用户的id
     */
    public static int userId = 0;
    /**
     * 用户的id
     */
    public static String userKey = "";

    /**
     * 是否已经登录
     */
    public static boolean logged = false;
    /**
     * 聊天的token
     */
    public static String rongyunToken = "";
    public static int tryTimes = 0;

    /**
     * 签名
     */
    public static String sign = "";
    /**
     * 性别
     */
    public static int sex = 0;

    public static boolean isMale(){
        return sex == Common.MALE;
    }

    public static String getSex(){
        return (sex == Common.MALE) ? "男" : "女";
    }

}
