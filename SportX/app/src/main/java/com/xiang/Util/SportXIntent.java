package com.xiang.Util;

import android.content.Context;
import android.content.Intent;

import com.xiang.sportx.UserDetailActivity;
import com.xiang.sportx.UserListActivity;

/**
 * Created by 祥祥 on 2016/5/25.
 */
public class SportXIntent {
    public static void gotoUserDetail(Context context, int userId, String username){
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra(Constant.USER_ID, userId);
        intent.putExtra(Constant.USER_NAME, username);
        context.startActivity(intent);
    }

    /**
     *
     * @param context
     * @param relatedUserId
     * @param username
     * @param findWhat Constant.FIND_FENSI or guanzhu
     */
    public static void gotoUserListActivity(Context context, int relatedUserId, String username, int findWhat){
        Intent intent = new Intent(context, UserListActivity.class);
        intent.putExtra(Constant.USER_ID, relatedUserId);
        intent.putExtra(Constant.USER_NAME, username);
        intent.putExtra(Constant.FIND_WHAT, findWhat);
        context.startActivity(intent);
    }
}
