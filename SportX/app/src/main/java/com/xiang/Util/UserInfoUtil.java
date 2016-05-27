package com.xiang.Util;

import android.net.Uri;

import com.xiang.database.helper.BriefUserHelper;
import com.xiang.database.model.TblBriefUser;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by 祥祥 on 2016/5/25.
 */
public class UserInfoUtil {
    public static void updateSavedUserInfo(int userId, String userName, String avatarUrl, BriefUserHelper briefUserHelper){
        // 更新融云聊天信息
        Uri uri = Uri.parse(avatarUrl);
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(userId + "", userName, uri));

        if(briefUserHelper != null){
            briefUserHelper.saveUser(
                    new TblBriefUser(userId + "", userName, avatarUrl)
            );
        }
    }
}
