package com.meibanlu.driver.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.tool.T;

/**
 * DbHelper1第一版本的数据库
 * Created by lhq on 2017/8/29.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper dbHelper;
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "Driver.db";
    private static final String TABLE_NAME = "message";
    /**
     * 记录位置的表格名字
     */
    private static final String TABLE_POSITION_NAME = "position";
    /**
     * 记录司机实时位置
     */
    private static final String TABLE_GPS_NAME = "gps";
    /**
     * 记录司机实时位置
     */
    private static final String TABLE_TRIP_FAIL = "tripfail";
    /**
     * 版本号
     */
    private static final int VERSION = 3;
    private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "isRead INTEGER,"
            + "receiveTime VARCHAR(20),"
            + "msgId VARCHAR(20),"
            + "information VARCHAR(255))";
    private static final String CREATE_TABLE_POSITION = "CREATE TABLE IF NOT EXISTS " + TABLE_POSITION_NAME + "("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "tripId INTEGER,"
            + "latitude DOUBLE,"
            + "longitude DOUBLE,"
            + "speed FLOAT,"
            + "bearing FLOAT,"
            + "time LONG)";
    private static final String CREATE_TABLE_GPS = "CREATE TABLE IF NOT EXISTS " + TABLE_GPS_NAME + "("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "gps VARCHAR(40),"
            + "time LONG)";
    private static final String CREATE_TABLE_TRIP_FAIL = "CREATE TABLE IF NOT EXISTS " + TABLE_TRIP_FAIL + "("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "tripId INTEGER,"
            + "status INTEGER,"
            + "mode INTEGER,"
            + "signArriveId INTEGER,"
            + "lngLat VARCHAR(255),"
            + "time LONG,"
            + "unique('tripId', 'status'))";

    private DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DbHelper getInstance() {
        if (dbHelper == null) {
            dbHelper = new DbHelper(DriverApplication.getApplication());
        }
        return dbHelper;
    }

    //版本不更新，并且没得数据库的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库
        T.log("启动创建数据库");
        db.execSQL(CREATE_TABLE_MESSAGE);
        db.execSQL(CREATE_TABLE_POSITION);
        db.execSQL(CREATE_TABLE_GPS);
        db.execSQL(CREATE_TABLE_TRIP_FAIL);
    }

    //版本更新时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        T.log("更新数据库");
        switch (oldVersion) {
            case 1:
                //数据库升级之前版本为1的时候，增加一个字段phone
//                db.execSQL("ALTER TABLE book ADD phone VARCHAR(20)");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITION_NAME);
                break;
            case 2:
                db.execSQL(CREATE_TABLE_TRIP_FAIL);
                break;
            default:
        }
        onCreate(db);
    }
}

