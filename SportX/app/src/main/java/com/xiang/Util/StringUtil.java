package com.xiang.Util;

/**
 * Created by 祥祥 on 2016/3/24.
 */
public class StringUtil {

    private static final char[] characters = {'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m',
             'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',
                     '1','2','3','4','5','6','7','8','9','0'};
    private static final int count = characters.length;

    public static boolean isNullOrEmpty(String string){
        if(string == null || string.equals("")){
            return true;
        } else{
            return false;
        }
    }

    /**
     * 生成七牛的key
     * @return
     */
    public static String generatorQiniuKey(){
        StringBuilder sb = new StringBuilder("00" + SportTimeUtil.getCurrentFileName());
        for(int i = 0; i < 8; i ++){
            sb.append(characters[((int) (Math.random() * count))]);
        }
        sb.append(".jpg");
        return sb.toString();
    }

    public static String generatorTrendKey(){
        StringBuilder sb = new StringBuilder("01" + SportTimeUtil.getCurrentFileName());
        for(int i = 0; i < 8; i ++){
            sb.append(characters[((int) (Math.random() * count))]);
        }
        sb.append(".jpg");
        return sb.toString();
    }

    public static boolean isNotEmpty(String s){
        if(s == null || s.equals("")){
            return false;
        }

        return true;
    }
}
