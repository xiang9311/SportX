package com.xiang.request;

/**
 * Created by 祥祥 on 2016/3/10.
 */
public class Url {

    public static String SERVER_IP = "http://101.200.84.75:8999/server/";

    public static String test = SERVER_IP + "test";
    public static String URL_GET_ARTICLES = SERVER_IP + "articles/getArticles";
    public static String URL_GET_DETAIL_ARTICLES = SERVER_IP + "articles/getDetailArticles";
    public static String URL_COLLECT_ARTICLES = SERVER_IP + "articles/collectArticles";
    public static String URL_COUNT = SERVER_IP + "articles/count";
    public static String URL_GET_USER_DETAIL = SERVER_IP + "pilot/getUserDetail";
    public static String URL_GET_QINIU_TOKEN = SERVER_IP + "pilot/getQiniuToken";
    public static String URL_REGISTER = SERVER_IP + "pilot/register";
    public static String URL_LOGIN = SERVER_IP + "pilot/login";
    public static String URL_GET_COLLECTS = SERVER_IP + "pilot/getMyCollection";
    public static String URL_SEARCH_ARTITLES = SERVER_IP + "articles/searchArticles";

}
