package com.xiang.request;

/**
 * Created by 祥祥 on 2016/5/19.
 */
public class UrlUtil {
    public static final String WEB_URL = "http://www.sportx-park.com";
    public static final String LOCAL_TEST_URL = "http://192.168.0.109:8000";
    public static final String WEB_URL_LOCAL = "http://www.sportx-park.com:8000";

    public static final boolean uselocal = false;
    public static final boolean useweblocal = false;

    public static String BASE_URL;

    static {
        if (uselocal){
            BASE_URL = LOCAL_TEST_URL;
        } else if(useweblocal){
            BASE_URL = WEB_URL_LOCAL;
        } else{
            BASE_URL = WEB_URL;
        }
    }


    public static final String URL_GET_QINIU_TOKEN = BASE_URL + "/token/getQiniuToken";
    public static final String URL_GET_RONGYUN_TOKEN = BASE_URL + "/token/getRongyunToken";

    /**
     * pilot
     */
    public static final String URL_REGISTER = BASE_URL + "/pilot/register";
    public static final String URL_LOGIN = BASE_URL + "/pilot/login";
    public static final String URL_GET_MY_INFO = BASE_URL + "/pilot/getMyInfo";
    public static final String URL_UPDATE_MY_INFO = BASE_URL + "/pilot/updateMyInfo";
    public static final String URL_GET_TRENDS = BASE_URL + "/pilot/getMyTrend";
    public static final String URL_GET_MY_COMMENT_MESSAGE = BASE_URL + "/pilot/getMyCommentMessage";
    public static final String URL_DELETE_MY_COMMENT_MESSAGE = BASE_URL + "/pilot/deleteCommentMessage";
    public static final String URL_GET_MY_XMONEY = BASE_URL + "/pilot/getMyXMoney";
    public static final String URL_GET_USER_GUANZHU = BASE_URL + "/pilot/getUserGuanzhu";
    public static final String URL_GET_USER_FENSI = BASE_URL + "/pilot/getUserFensi";
    public static final String URL_GUANZHU_USER = BASE_URL + "/pilot/guanzhuUser";
    public static final String URL_GET_USER_DETAIL = BASE_URL + "/pilot/getUserDetail";
    public static final String URL_SEARCH_USER = BASE_URL + "/pilot/searchUser";
    public static final String URL_SEARCH_GYM = BASE_URL + "/pilot/searchGym";
    public static final String URL_GET_HOT_SEARCH_KEY = BASE_URL + "/pilot/getSearchKeys";
    public static final String URL_VERIFY_PHONE_CAN_USE = BASE_URL + "/pilot/verifyPhoneCanUse";
    public static final String URL_GET_TREND_BRIEF_MESSAGE = BASE_URL + "/pilot/getTrendBriefMessage";
    public static final String URL_GET_BRIEF_USER = BASE_URL + "/pilot/getBriefUser";
    public static final String URL_GET_RECOMMEND_USER = BASE_URL + "/pilot/getRecommendUser";

    /**
     * trend
     */
    public static final String URL_CREATE_TREND = BASE_URL + "/trend/createTrend";
    public static final String URL_GET_FOLLOW_TREND = BASE_URL + "/trend/getMyFollowTrends";
    public static final String URL_COMMENT_TREND = BASE_URL + "/trend/commentTrend";
    public static final String URL_GET_COMMENT = BASE_URL + "/trend/getTrendComment";
    public static final String URL_LIKE_TREND = BASE_URL + "/trend/likeTrend";
    public static final String URL_GET_TREND_BY_ID = BASE_URL + "/trend/getTrendById";

    /**
     * gym
     */
    public static final String URL_GET_GYM_LIST = BASE_URL + "/gym/getGymList";
    public static final String URL_GET_DETAIL_GYM = BASE_URL + "/gym/getGymDetail";
    public static final String URL_GET_RECOMMEND_GYM = BASE_URL + "/gym/getRecommendGym";
    public static final String URL_GET_GYM_TREND = BASE_URL + "/gym/getGymTrend";

}
