package com.meibanlu.driver.tool;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amap.api.trace.TraceLocation;
import com.meibanlu.driver.sql.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhq on 2017-11-18.
 */

public class DBManager {
    private static DbHelper helper;
    private static SQLiteDatabase sqliteDb;
    /**
     * 记数器 应该设置静态的类变量
     *
     * @param context
     */
    private static int mCount;
    //同一个数据库连接
    private static DBManager dbManager;

    private DBManager() {
        helper = DbHelper.getInstance();
    }

    //单例
    static synchronized DBManager getInstance() {
        if (dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    synchronized void openDb() {
        if (mCount == 0) {
            sqliteDb = helper.getWritableDatabase();
        }
        mCount++;
    }

    synchronized void closeDb() {
        mCount--;
        if (mCount == 0) {
            sqliteDb.close();
        }
    }

    //增加字段
    public void insert(MessageBean msgBean) {
        //http://blog.csdn.net/jason0539/article/details/10248457
        if (msgBean != null) {
            sqliteDb.execSQL("insert into message(isRead,receiveTime,msgId,information) values(?,?,?,?)", new Object[]{msgBean.getIsRead(), msgBean.getReceiveTime(), msgBean.getMsgId(), msgBean.getInformation()});
            closeDb();
        }
    }

    //查询所有字段
    public List<MessageBean> query() {
        //http://blog.csdn.net/jason0539/article/details/10248457
        Cursor cursor = sqliteDb.rawQuery("select * from message", null);
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
        cursor.close();
        closeDb();
        return listBook;
    }

    //条件查询 queryContent,前面几个参数为要查询出来的内容，比如 name，id，author ；后面两个为条件选择的类型，具体值
    public List<MessageBean> query(String... queryContent) {
        //http://blog.csdn.net/jason0539/article/details/10248457
//        SQLiteDatabase db = getWritableDatabase();
        String[] strContent = null;
        if (queryContent.length > 2) {
            strContent = new String[queryContent.length - 2];
            for (int i = 0; i < strContent.length; i++) {
                strContent[i] = queryContent[i];
            }
        }

        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        Cursor cursor = sqliteDb.query("message", strContent, queryContent[queryContent.length - 2] + "=?", new String[]{queryContent[queryContent.length - 1]}, null, null, null);
        //模糊查询
//        Cursor cursor = db.query("book",strContent,queryContent[queryContent.length - 2]+"like '%" + "一" + "%'",new String[]{queryContent[queryContent.length - 1]},null,null,null);
//        String querySql = "select * from book where name  like '%++%'";
//        Cursor cursor = db.rawQuery(querySql, null);
        List<MessageBean> listBook;
        listBook = dealData(cursor);
        cursor.close();
        closeDb();
        return listBook;
    }

    //条件模糊查询 queryContent,前面几个参数为要查询出来的内容，比如 name，id，author ；后面两个为条件选择的类型，具体值
    public List<MessageBean> dimQuery(String... queryContent) {
        //http://blog.csdn.net/jason0539/article/details/10248457
//        SQLiteDatabase db = getWritableDatabase();
        String[] strContent = null;
        if (queryContent.length > 2) {
            strContent = new String[queryContent.length - 2];
            for (int i = 0; i < strContent.length; i++) {
                strContent[i] = queryContent[i];
            }
        }

        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        //模糊查询
        String querySql = "select * from book where " + queryContent[queryContent.length - 2] + " like '%" + queryContent[queryContent.length - 1] + "%'";
        Cursor cursor = sqliteDb.rawQuery(querySql, null);
        List<MessageBean> listBook;
        listBook = dealData(cursor);
        cursor.close();
        closeDb();
        return listBook;
    }

    //删除
    public void delete(String id) {
        sqliteDb.delete("book", "id=?", new String[]{id});
        closeDb();
    }

    //删除表格所有的数据
    public void deleteAll() {
        sqliteDb.execSQL("DELETE FROM " + "message");
        closeDb();
    }

    //修改
    public void update(int id, MessageBean msgBean) {
        if (msgBean != null) {
            String changeId = id + "";
            ContentValues values = new ContentValues();
            values.put("isRead", msgBean.getIsRead());
            sqliteDb.update("message", values, "id=?", new String[]{changeId});
            closeDb();
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

    /**
     * 增加位置信息字段
     *
     * @param positionBean positionBean
     */
    public void insertPosition(PositionBean positionBean) {
        if (positionBean != null) {
            sqliteDb.execSQL("insert into position(tripId,latitude,longitude,speed,bearing,time) values(?,?,?,?,?,?)", new Object[]{positionBean.getTripId(), positionBean.getLatitude(), positionBean.getLongitude(), positionBean.getSpeed(), positionBean.getBearing(), positionBean.getTime()});
            closeDb();
        }
    }

    public void deleteTripId(String tripId) {
        sqliteDb.delete("position", "tripId=?", new String[]{tripId});
        closeDb();
    }

    //删除表格所有的数据
    public void deleteAllTripId() {
        sqliteDb.execSQL("DELETE FROM " + "position");
        closeDb();
    }

    //根据tripId查询内容
    public List<TraceLocation> queryAllPosition(String tripId) {
        Cursor cursor = sqliteDb.query("position", null, "tripId = ?", new String[]{tripId}, null, null, null);
        List<TraceLocation> listTrace;
        listTrace = dealPositionData(cursor);
        cursor.close();
        closeDb();
        return listTrace;
    }

    private List<TraceLocation> dealPositionData(Cursor cursor) {
        List<TraceLocation> listTrace = new ArrayList<>();
        while (cursor.moveToNext()) {
            TraceLocation traceLocation = new TraceLocation();
            traceLocation.setLatitude(cursor.getDouble(2));
            traceLocation.setLongitude(cursor.getDouble(3));
            traceLocation.setSpeed(cursor.getFloat(4));
            traceLocation.setBearing(cursor.getFloat(5));
            traceLocation.setTime(cursor.getLong(6));
            listTrace.add(traceLocation);
        }
        return listTrace;
    }

    //插入gps数据
    public boolean insertGps(GpsBean gpsBean) {
        if (gpsBean != null) {
            sqliteDb.execSQL("insert into gps(gps, time) values(?,?)", new Object[]{gpsBean.getGps(), gpsBean.getTime()});
            closeDb();
            return true;
        }
        return false;
    }

    public List<GpsBean> gpsBeans() {
        Cursor cursor = sqliteDb.query("gps", null, null, null, null, null, null);
        List<GpsBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            GpsBean gpsBean = new GpsBean();
            gpsBean.setId(cursor.getInt(0));
            gpsBean.setGps(cursor.getString(1));
            gpsBean.setTime(cursor.getLong(2));
            list.add(gpsBean);
        }
        cursor.close();
        closeDb();
        return list;
    }

    /**
     * @param ids 1,2,3,4
     * @return 返回boolean
     */
    public boolean deleteWithId(String ids) {
        try {
            sqliteDb.execSQL("DELETE FROM gps where id in (" + ids + ")");
            closeDb();
        } catch (Exception e) {
            T.log(e.getLocalizedMessage());
            return false;
        }
        return false;
    }

    /**
     * @return 返回boolean
     */
    public boolean gpsDeleteAll() {
        try {
            sqliteDb.execSQL("DELETE FROM gps");
        } catch (Exception e) {
            T.log(e.getLocalizedMessage());
            return false;
        } finally {
            closeDb();
        }
        return false;
    }

    public boolean insertTripFail(TripFailBean bean) {
        try {
            if (bean != null) {
                sqliteDb.execSQL(
                        "insert into tripfail(tripId, status, mode, lngLat, signArriveId, time) values(?,?,?,?,?,?)",
                        new Object[]{bean.getTripId(), bean.getStatus(), bean.getMode(), bean.getLngLat(), bean.getSignArriveId(), bean.getTime()});

                return true;
            }
        } catch (Exception e) {
            //如果出现班次id和status重复，直接跳过。
            T.log("重复插入失败班次打卡: tripId = " + bean.getTripId());
        } finally {
            closeDb();
        }
        return false;
    }

    public List<TripFailBean> getTripFails() {
        Cursor cursor = sqliteDb.query("tripfail", null, null, null, null, null, null);
        List<TripFailBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            TripFailBean tripFailBean = new TripFailBean();
            tripFailBean.setId(cursor.getInt(0));
            tripFailBean.setTripId(cursor.getInt(1));
            tripFailBean.setStatus(cursor.getInt(2));
            tripFailBean.setMode(cursor.getInt(3));
            tripFailBean.setSignArriveId(cursor.getInt(4));
            tripFailBean.setLngLat(cursor.getString(5));
            tripFailBean.setTime(cursor.getLong(6));
            list.add(tripFailBean);
        }
        cursor.close();
        closeDb();
        return list;
    }

    public boolean delTripFail(Integer tripId, Integer status) {
        try {
            sqliteDb.execSQL("DELETE FROM tripfail where tripId = " + tripId + " and status = " + status);
            return true;
        } catch (Exception e) {
            T.log(e.getLocalizedMessage());
        } finally {
            closeDb();
        }
        return false;
    }


    /**
     * 失败的某个id是否存在，不存在在进行下面的判断
     *
     * @param id 失败id
     * @return true or false
     */
    public boolean tripFailExist(Integer id) {
        Cursor cursor = null;
        try {
            cursor = sqliteDb.rawQuery("select id from tripfail where id = " + id, null);
            return cursor.moveToNext();
        } catch (Exception e) {
            T.log(e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDb();
        }
        return false;
    }

}
