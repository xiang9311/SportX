package com.xiang.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiang.database.common.DataBaseCommon;

/**
 * Created by 祥祥 on 2016/6/12.
 */
public class GymScoreHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "tbl_gym_score";
    private static final int VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_GYM_ID = "gym_id";
    private static final String KEY_SCORE = "score";

    //create sql
    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "("+KEY_ID+" integer primary key autoincrement, "
            + KEY_GYM_ID + " integer not null, "
            + KEY_SCORE + " integer not null) ";


    public GymScoreHelper(Context context){
        super(context, DataBaseCommon.DATABASE_GYM_SCORE, null, VERSION);
    }

    public GymScoreHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public GymScoreHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
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

    // 查看gymdetail时候需要调用该函数加分
    public void whenGetGymDetail(int gymId){
        addGymScore(gymId, 1);
    }

    // 引用gym作为trend或评论时调用该函数加分
    public void whenUseGym(int gymId){
        addGymScore(gymId, 5);
    }

    private void addGymScore(int gymId, int addScore){
        //
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor_has = db.query(TABLE_NAME
                , new String[]{KEY_ID, KEY_GYM_ID, KEY_SCORE}
                , KEY_GYM_ID + "=?", new String[]{gymId + ""}, null, null, null);
        int id = -1;
        int score = 0;
        if(cursor_has.moveToFirst()){
            id = cursor_has.getInt(0);
            score = cursor_has.getInt(2);
        }

        cursor_has.close();

        if(id == -1){
            // insert
            SQLiteDatabase db_insert = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_GYM_ID, gymId);
            values.put(KEY_SCORE, 1);
            db_insert.insert(TABLE_NAME, null, values);
            return;
        } else {
            // update
            SQLiteDatabase db_update = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_GYM_ID, gymId);
            values.put(KEY_SCORE, score + addScore);
            db_update.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{id + ""});
            return ;
        }

    }

    /**
     * 如果是0，则表示没有数据
     * @return
     */
    public int getHighScoreGymId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME
                , new String[]{KEY_ID, KEY_GYM_ID, KEY_SCORE}
                , null, null, null, null, KEY_SCORE);
        if(cursor.moveToLast()){
            int gymId = cursor.getInt(1);
            cursor.close();
            return gymId;
        }
        return 0;
    }
}
