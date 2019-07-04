package com.meibanlu.driver.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.meibanlu.driver.application.DriverApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * DbHelper1第一版本的数据库
 * Created by lhq on 2017/8/29.
 */

public class DbPosHelper extends SQLiteOpenHelper {
    //知识链接
    private static DbPosHelper dbPoSHelper;
    private static SQLiteDatabase db;
    private static final String DB_NAME = "driverPosition.db";//数据库名字
    private static final int VERSION = 1;//版本号

    private DbPosHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DbPosHelper getInstance() {
        if (dbPoSHelper == null) {
            dbPoSHelper = new DbPosHelper(DriverApplication.getApplication());
        }
        return dbPoSHelper;
    }


    //版本不更新，并且没得数据库的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        DbPosHelper.db = db;
    }

    //版本更新时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 创建表格.表格名字
     */
    public void createTable(String tableName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "latitude DOUBLE,"
                + "longitude DOUBLE,"
                + "speed FLOAT,"
                + "bearing FLOAT,"
                + "time LONG)");//创建数据库
    }

    //增加字段
    public void insert(PositionBean positionBean, String tableName) {
        if (positionBean != null) {
            db.execSQL("insert into " + tableName + "(latitude,longitude,speed,bearing,time) values(?,?,?,?,?)", new Object[]{positionBean.getLatitude(), positionBean.getLongitude(), positionBean.getSpeed(), positionBean.getBearing(), positionBean.getTime()});
            db.close();
        }
    }

    //查询所有字段
    public List<PositionBean> query(String tableName) {
        Cursor cursor = db.rawQuery("select * from " + tableName, null);
        List<PositionBean> listPosition = new ArrayList<>();
        while (cursor.moveToNext()) {
            PositionBean positionBean = new PositionBean();
            positionBean.setId(cursor.getInt(0));
            positionBean.setLatitude(cursor.getDouble(1));
            positionBean.setLongitude(cursor.getDouble(2));
            positionBean.setSpeed(cursor.getFloat(3));
            positionBean.setBearing(cursor.getFloat(4));
            positionBean.setTime(cursor.getLong(5));
            listPosition.add(positionBean);
        }
        cursor.close();
        db.close();
        return listPosition;
    }

    //删除
    public void delete(String id) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete("book", "id=?", new String[]{id});
    }

    //删除表格
    public void deleteTable(String tableName) {
        db.execSQL("drop table if exists " + tableName);
    }

    //修改
    public void update(int id, MessageBean msgBean) {
        if (msgBean != null) {
            String changeId = id + "";
            ContentValues values = new ContentValues();
            values.put("isRead", msgBean.getIsRead());
            SQLiteDatabase db = getReadableDatabase();
            db.update("message", values, "id=?", new String[]{changeId});
            db.close();
        }
    }


    private List<MessageBean> dealData(Cursor cursor) {
        List<MessageBean> listBook = new ArrayList<>();
        while (cursor.moveToNext()) {
            MessageBean messageBean = new MessageBean();
            messageBean.setId(cursor.getInt(0));
            messageBean.setIsRead(cursor.getInt(1));
            messageBean.setReceiveTime(cursor.getString(2));
            messageBean.setMsgId(cursor.getString(3));
            messageBean.setInformation(cursor.getString(4));
            listBook.add(messageBean);
        }
        return listBook;
    }

}
