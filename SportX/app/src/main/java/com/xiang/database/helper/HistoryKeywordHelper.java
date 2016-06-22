package com.xiang.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiang.database.common.DataBaseCommon;
import com.xiang.database.model.TblHistoryKeyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 祥祥 on 2016/6/6.
 */
public class HistoryKeywordHelper extends SQLiteOpenHelper{

    private static final String TABLE_NAME = "tbl_key_word";
    private static final int VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_KEYWORD = "key_word";
    private static final String KEY_ORDER = "key_order";

    //create sql
    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "("+KEY_ID+" integer primary key autoincrement, "
            + KEY_KEYWORD + " text not null, "
            + KEY_ORDER + " integer not null) ";       //秒 时间戳

    public HistoryKeywordHelper(Context context){
        super(context, DataBaseCommon.DATABASE_HISTORY_KEYWORD, null, VERSION);
    }

    public HistoryKeywordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public HistoryKeywordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
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

    /**
     * 获取 history keyword 列表
     * @return
     */
    public List<TblHistoryKeyword> getHistoryKeywords(){
        List<TblHistoryKeyword> tblHistoryKeywords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor
        Cursor cursor = db.query(TABLE_NAME
                , new String[]{KEY_ID, KEY_KEYWORD, KEY_ORDER}
                , null, null, null, null, KEY_ORDER);
        TblHistoryKeyword tblHistoryKeyword = null;
        // 注意返回结果可能为空
        if (cursor.moveToLast()) {
            do {
                tblHistoryKeyword = new TblHistoryKeyword(cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getInt(2));
                tblHistoryKeywords.add(tblHistoryKeyword);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return tblHistoryKeywords;
    }

    public long addHistoryKeyword(String keywords){
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor
        Cursor cursor = db.query(TABLE_NAME
                , new String[]{KEY_ID, KEY_KEYWORD, KEY_ORDER}
                , null, null, null, null, KEY_ORDER);
        // 注意返回结果可能为空
        int lastOrder = -1;
        if (cursor.moveToLast()) {
            lastOrder = cursor.getInt(2);
        }
        lastOrder ++;

        // 查询获取到的已经存在的keyword的id
        Cursor cursor_has = db.query(TABLE_NAME
                , new String[]{KEY_ID, KEY_KEYWORD, KEY_ORDER}
                , KEY_KEYWORD + "=?", new String[]{keywords}, null, null, KEY_ORDER);
        int id = -1;
        if(cursor_has.moveToFirst()){
            id = cursor_has.getInt(0);
        }

        if(id == -1){
            // insert
            SQLiteDatabase db_insert = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_KEYWORD, keywords);
            values.put(KEY_ORDER, lastOrder);
            return db_insert.insert(TABLE_NAME, null, values);
        } else {
            // update
            SQLiteDatabase db_update = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_KEYWORD, keywords);
            values.put(KEY_ORDER, lastOrder);
            int result = db_update.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{id + ""});
            return result;
        }
    }

    public void removeAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        // 删除
        db.delete(TABLE_NAME, null, null);

        return ;
    }
}
