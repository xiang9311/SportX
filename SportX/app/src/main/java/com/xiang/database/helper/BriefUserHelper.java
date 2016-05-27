package com.xiang.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xiang.database.common.DataBaseCommon;
import com.xiang.database.model.TblBriefUser;

/**
 * Created by 祥祥 on 2016/5/20.
 */
public class BriefUserHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "tbl_brief_user";
    private static final int VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_AVATAR = "user_avatar";

    //create sql
    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "("+KEY_ID+" text primary key, "
            + KEY_USER_NAME + " text not null, "
            + KEY_USER_AVATAR + " text not null) ";       //秒 时间戳

    public BriefUserHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DataBaseCommon.DATABASE_USER, null, VERSION);
    }

    public BriefUserHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DataBaseCommon.DATABASE_USER, null, VERSION, errorHandler);
    }

    public BriefUserHelper(Context context){
        super(context, DataBaseCommon.DATABASE_USER, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * 存储一条用户信息
     * @param tblBriefUser
     */
    public void saveUser(TblBriefUser tblBriefUser){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, tblBriefUser.id);
        values.put(KEY_USER_NAME, tblBriefUser.userName);
        values.put(KEY_USER_AVATAR, tblBriefUser.userAvatar);
        if(db.insert(TABLE_NAME, null, values) == -1){
            Log.d("database","save error , to update");
            updateUser(tblBriefUser);
        }
    }

    /**
     *
     * @param tblBriefUser
     * @return
     */
    private boolean updateUser(TblBriefUser tblBriefUser){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, tblBriefUser.userName);
        values.put(KEY_USER_AVATAR, tblBriefUser.userAvatar);
        int result = db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{tblBriefUser.id});
        if(result != 0) {
            return true;
        } else{
            return false;
        }
    }

    /**
     * 如果不存在，返回null
     * @param id
     * @return
     */
    public TblBriefUser getUserById(String id){
        SQLiteDatabase db = this.getWritableDatabase();

        // Cursor
        Cursor cursor = db.query(TABLE_NAME
                , new String[]{KEY_ID, KEY_USER_NAME, KEY_USER_AVATAR}
                , KEY_ID+"=?", new String[]{id}, null, null, null);
        TblBriefUser tblBriefUser = null;
        // 注意返回结果可能为空
        if (cursor.moveToFirst()) {
            tblBriefUser = new TblBriefUser(
                    cursor.getString(0)
                    , cursor.getString(1)
                    , cursor.getString(2));
        }
        return tblBriefUser;
    }
}
