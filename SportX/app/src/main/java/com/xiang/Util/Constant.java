package com.xiang.Util;

import android.os.Environment;

import com.xiang.sportx.R;

import java.io.File;

/**
 * Created by 祥祥 on 2016/3/3.
 */
public class Constant {

    public static final boolean DEBUG = true;
    public static final boolean DEBUG_IMAGELOADER = false;

    public static final String SP_DATA = "sp_data";
    public static final String DEFAULT_AVATAR_INDEX = "default_avatar_index";

    public static final String USER_ID = "user_id";
    public static final String URL = "url";
    public static final String ARTICLE = "article";
    public static final String ARTICLE_ID = "article_id";
    public static final String CATEGORY = "category";
    public static final String FRAGMENT_INDEX = "fragment_index";
    public static final String DATA_INDEX = "data_index";
    public static final String FROM = "from";
    public static final String USER_NAME = "user_name";
    public static final String LOGIN_USER_NAME = "login_user_name";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String REMEMBER = "remember";
    public static final String AUTO_LOGIN = "autologin";
    public static final String CKECKED = "checked";
    public static final String LAST_CHECKED_TIME = "last_checked_time";
    public static final String IMAGES = "images";
    public static final String CURRENT_INDEX = "current_index";

    public static final int FROM_PAGER = 1;
    public static final int FROM_USER_DETAIL = 2;
    public static final int FROM_COLLECT = 3;
    public static final int FROM_SEARCH = 4;

    public static final String HEADER_CMDID = "cmdid";

    public static final String HEADER_TIMESTAMP = "timestamp";

    /**
     * 位于中间的点
     */
    public static final String PONITER_CENTER = "•";

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * resId
     * @param index
     * @return
     */
    public static int getDefaultAvatar(int index){
        int[] strings = {R.mipmap.avatar_01, R.mipmap.avatar_02, R.mipmap.avatar_03, R.mipmap.avatar_04, R.mipmap.avatar_05,
                R.mipmap.avatar_06, R.mipmap.avatar_07, R.mipmap.avatar_08, R.mipmap.avatar_09, R.mipmap.avatar_10,
                R.mipmap.avatar_11};

        if(index > 0 && index < strings.length){
            return strings[index];
        }

        return R.mipmap.avatar_01;
    }

    public static int getDefaultAvatarSize(){
        return 11;
    }


    /**
     * 获取头像resId列表
     * @param index
     * @return
     */
    public static int[] getDefaultAvatars(int index){
        int[] strings = {R.mipmap.avatar_01, R.mipmap.avatar_02, R.mipmap.avatar_03, R.mipmap.avatar_04, R.mipmap.avatar_05,
                R.mipmap.avatar_06, R.mipmap.avatar_07, R.mipmap.avatar_08, R.mipmap.avatar_09, R.mipmap.avatar_10,
                R.mipmap.avatar_11};
        return strings;
    }

    /***文章分类***/
    /**
     * web
     */
    public static final int ARTICLE_WEB = 1;
    /**
     * 普通
     */
    public static final int ARTICLE_NORMAL = 2;
    /**
     * image
     */
    public static final int ARTICLE_IMAGE = 3;



    /*******************  froms  *********************/
    public static final int FROM_FOLLOW = 1;
    public static final int FROM_GYM_DETAIL = 2;

}
