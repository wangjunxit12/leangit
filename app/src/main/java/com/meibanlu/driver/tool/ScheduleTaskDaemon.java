package com.meibanlu.driver.tool;

import android.location.Location;
import android.text.TextUtils;

import com.meibanlu.driver.OnPollingListener;
import com.meibanlu.driver.R;
import com.meibanlu.driver.activity.HomePageActivity;
import com.meibanlu.driver.activity.RunMapActivity;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.bean.LineStation;
import com.meibanlu.driver.bean.PositionVo;
import com.meibanlu.driver.bean.TaskDetail;
import com.meibanlu.driver.bean.UpdateEvent;
import com.meibanlu.driver.sql.TripFailBean;
import com.meibanlu.driver.tool.web.CallBack;
import com.meibanlu.driver.tool.web.DataCallBack;
import com.meibanlu.driver.tool.web.SimpleCallBack;
import com.meibanlu.driver.tool.web.WebService;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.requeset.UpLoadLocationRequest;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 启动线程处理gps和司机打卡
 *
 * @author Administrator
 */
public class ScheduleTaskDaemon implements OnPollingListener{

    private static Thread gpsThread;
    private static Thread signThread;

    private static volatile ScheduleTaskDaemon instance;

    public static ScheduleTaskDaemon getInstance(){
        if(instance==null){
            instance=new ScheduleTaskDaemon();
        }
        return instance;
    }

    private ScheduleTaskDaemon() {
    }

    static void startGpsTask() {
        if (gpsThread == null) {
            gpsThread = new Thread(new GpsTask());
            gpsThread.start();
        }
    }

    @Override
    public void onTimer() {
        if(UtilTool.isBackground(DriverApplication.getApplication())){
            DriverLocation.getInstance().startBackGroundLocation();
        }else {
            DriverLocation.getInstance().stop();
        }
        final PositionVo judgePosition = CommonData.judgePosition;
        //首先处理本地是否有未上传的打卡
        List<TripFailBean> tripFailBeans = UtilTool.getDbManager().getTripFails();
        checkSchedule(CommonData.listTask);
        if (!tripFailBeans.isEmpty()) {
            try {
                for (TripFailBean bean : tripFailBeans) {
                    sign(null, bean.getTripId(), bean.getStatus(), bean.getSignArriveId(), bean.getLngLat(), null, bean.getMode());
                    boolean isExist = UtilTool.getDbManager().tripFailExist(bean.getId());
                    if (isExist){
                        //如果没有删除，则不再继续
                        break;
                    }
                }
                //再次查询
                tripFailBeans = UtilTool.getDbManager().getTripFails();
                if (tripFailBeans.isEmpty()) {
                    XmPlayer.getInstance().playTTS("离线补卡成功");
                    //直接刷新列表
                    UtilTool.getInstance().getSchedules();
                    RxBus.getInstance().post(new UpdateEvent(true));
//                            UtilTool.refreshTodayTask();
                }
            } catch (Exception e) {
                //转换失败，可能由于版本不同
                T.logE(e.getMessage());
            }
        }
        //然后继续判断
        if (judgePosition != null) {
            boolean needSelect = CommonData.holderTrip == null ||
                    (!CommonData.holderTrip.isStartOut() && !CommonData.holderTrip.isSetManual());
            if (needSelect) {
                //获取tripId打卡信息,根据时间更新班次信息
                TaskDetail taskDetail = selectCurrentTrip(CommonData.listTask,
                        judgePosition.getLongitude(),
                        judgePosition.getLatitude());
                if (taskDetail != null) {
                    if (CommonData.holderTrip != null) {
                        if (!CommonData.holderTrip.getTrip().equals(taskDetail)) {
                            CommonData.holderTrip = taskDetail.toHolderTrip();
                            UtilTool.changeHomeUi();
                        }
                    } else {
                        CommonData.holderTrip = taskDetail.toHolderTrip();
                        UtilTool.changeHomeUi();
                    }
                } else {
                    if (CommonData.holderTrip != null && !CommonData.holderTrip.getTrip().isRoll()) {
                        CommonData.holderTrip = null;
                        UtilTool.changeHomeUi();
                    }
                }
            }
            HolderTrip holderTrip = CommonData.holderTrip;
            if (holderTrip != null) {
                String time = holderTrip.getTrip().getSchedule();
                T.log(time);
//                        //判断是否插入数据库
//                        insertPositionSqlite(holderTrip, CommonData.aMapLocation);
                if (holderTrip.isStartOut()) {
                    if(holderTrip.getTrip().getStatus()==Constants.STATUS_PRE_DEPART){
                        sign(holderTrip, Constants.STATUS_DEPART, null, null, Constants.MODE_MAN_SUCCESS);
                    }
                    //到站打卡判断
                    driveEndSign(holderTrip, judgePosition, Constants.MODE_AUTO);
                    //异步上传位置，无须处理回调
                    uploadTripGps(judgePosition, holderTrip);
                } else {
                    //司机打卡
                    if(holderTrip.getTrip().getStatus()==Constants.STATUS_DEPART){
                        if(!holderTrip.isStartOut()){
                            holderTrip.setStartOut(true);
                        }
                    }
                    driveStartSign(holderTrip, judgePosition, Constants.MODE_AUTO);
                }
            }
        }

        //刷新显示状态
        HomePageActivity homePageActivity = HomePageActivity.getInstance();
        if (homePageActivity != null &&CommonData.listTask != null) {
            if(CommonData.listTask.size()>0){
                HomePageActivity.getInstance().freshState(UtilState.getState());
            }
        }
    }

    /**
     * 定时线程任务
     */
    private static class GpsTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                //处理gps上传
                final Location aMapLocation = CommonData.aMapLocation;
                if (aMapLocation != null) {
                    //处理定位异常 开始
                    Integer locationErrorCode = CommonData.locationErrorCode;
                    if (locationErrorCode != null && locationErrorCode != 0) {
//                        T.error("errorCode:" + locationErrorCode);
                    }
                    //处理定位异常 结束
                    if (CommonData.judgePosition != null) {
                        long timeInterval = System.currentTimeMillis() - CommonData.judgePosition.getEnterTime();
                        if (timeInterval > Constants.RESTART_TIME * 1000) {
                            DriverLocation.reStartLocation();
                            RectifyLocationUtils.clearPosition();
                        }
                    }
                }
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(Constants.GSP_UPLOAD_INTERVAL));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    gpsThread = null;
                    break;
                }
            }
        }
    }


    private TaskDetail selectCurrentTrip(List<TaskDetail> tasks, Double longitude, Double latitude) {
        if (tasks == null || tasks.size() == 0) {
            return null;
        }
        //要返回的task
        TaskDetail taskSelected = null, lastArrive = null;
        //不能兼容跨夜的问题
        String time = TimeTool.getCurrentTime("HH:mm");
        //正在运行的班次直接设置
        for (TaskDetail taskDetail : tasks) {
            if (taskDetail.getStatus() == Constants.STATUS_DEPART) {
                taskSelected = taskDetail;
            } else {
                if (taskDetail.getStatus() == Constants.STATUS_ARRIVE) {
                    lastArrive = taskDetail;
                }
            }
        }
        if (taskSelected != null) {
            return taskSelected;
        }

        List<TaskDetail> prepareList=new ArrayList<>();
        for(TaskDetail detail:tasks){
            if(detail.getStatus()==Constants.STATUS_PRE_DEPART){
                prepareList.add(detail);
            }
        }
        if(allIsRoll(prepareList)){
           Collections.sort(prepareList);
           if(isIn(prepareList)){
               int [] temps=new int[prepareList.size()];
               Map<Integer,Integer> map=new HashMap<>();
               int dis=0;
               for(int i=0;i<prepareList.size();i++){
                   //处理正常班次
                   String scheduleTime = prepareList.get(i).getSchedule();
                   dis=TimeTool.getDistanceTimes(scheduleTime, TimeTool.getCurrentTime("HH:mm"));
                   map.put(dis,i);
                   temps[i]=dis;
               }
               Arrays.sort(temps);
               Integer key=map.get(temps[0]);
               return prepareList.get(key);
           }else {
               for (int i = 0; i < prepareList.size(); i++) {
                   TaskDetail taskDetail = prepareList.get(i);
                   //处理正常班次
                   String scheduleTime = taskDetail.getSchedule();
                   //判断当前时间是否超过下一班, 超过下一班出发时间的话，跳过当前班次
                   if (i + 1 < prepareList.size()) {
                       if (TimeTool.getDistanceTimesSign(TimeTool.getCurrentTime("HH:mm"),scheduleTime) > 10) {
                           continue;
                       }
                   }
                   //当前班次与当前时间的分钟数差值
                   int duration = TimeTool.getDistanceTimesSign(time, scheduleTime);
                   if (duration >= Constants.TRIP_SELECT_TIME_LEFT && duration <= Constants.TRIP_SELECT_TIME_RIGHT) {
                       return taskDetail;
                   }
               }
           }
        }else {
            //根据最小时间差判断滚动班次
            int minimumDuration = Integer.MAX_VALUE;
            for (int i = 0; i < prepareList.size(); i++) {
                TaskDetail taskDetail = prepareList.get(i);
                if (taskDetail.getStatus() != Constants.STATUS_PRE_DEPART) {
                    continue;
                }
                if (!taskDetail.isRoll()) {
                    //处理正常班次
                    String scheduleTime = taskDetail.getSchedule();
                    //判断当前时间是否超过下一班, 超过下一班出发时间的话，跳过当前班次
                    if (i + 1 < prepareList.size()) {
                        if (TimeTool.getCurrentTime("HH:mm").compareTo(prepareList.get(i + 1).getSchedule()) > 10) {
                            continue;
                        }
                    }
                    //处理迟到打卡
                    if (lastArrive != null) {
                        String arr = TimeTool.stampToDate(lastArrive.getArriveTime(), "HH:mm");
                        if (scheduleTime.compareTo(arr) < 0) {
                            int duration = TimeTool.getDistanceTimes(arr, time);
                            if (duration <= Constants.TRIP_SELECT_TIME_RIGHT) {
//                            taskSelected = taskDetail;
                                taskDetail.setLastArriveTime(arr);
                                taskDetail.setLate(true);
                                return taskDetail;
//                            continue;
                            }
                        }
                    }
                    //当前班次与当前时间的分钟数差值
                    int duration = TimeTool.getDistanceTimesSign(time, scheduleTime);
                    if (duration >= Constants.TRIP_SELECT_TIME_LEFT && duration <= Constants.TRIP_SELECT_TIME_RIGHT) {
                        return taskDetail;
                    }
                } else if(GpsTool.checkPointInPolygon(
                        latitude,
                        longitude,
                        taskDetail.getDepartStation().getLngLat(),
                        taskDetail.getDepartStation().getAreaRadius())) {
                    //滚动班次先判断范围,再判断时间
                    int duration = TimeTool.getDistanceTimes(taskDetail.getSchedule(), time);
                    if (duration < minimumDuration) {
                        taskSelected = taskDetail;
                        minimumDuration = duration;
                    }
                }
            }
        }
        return taskSelected;
    }

    private void uploadTripGps(PositionVo positionVo, HolderTrip holderTrip) {
        T.log("打卡成功后上传的经纬度" + positionVo.getLatitude());
        String position = new DecimalFormat("#0.000000").format(positionVo.getLongitude()) + ";" + new DecimalFormat("#0.000000").format(positionVo.getLatitude());
        //出发成功，异步实时位置上传，不需要管回调
        RequestEnvelope envelope = new RequestEnvelope();
        RequestBody body = new RequestBody();
        UpLoadLocationRequest request = new UpLoadLocationRequest();
        Header header = new Header();
        request.setTime(TimeTool.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        request.setLongitude(position);
        request.setXl(holderTrip.getTrip().getId());
        body.locationRequest = request;
        envelope.header = header;
        envelope.body = body;
        WebService.getInstance().getData(envelope, "urn:TYWJAPPIntf-ITYWJAPP#weizhiinsert", new DataCallBack() {

            @Override
            public void success(ResponseEnvelope result) {

            }

            @Override
            public void error(String responseMessage) {

            }
        });
    }
    /**
     * 司机出发打卡
     */
    public static boolean driveStartSign(HolderTrip holderTrip, PositionVo positionVo, int mode) {
        T.log("driveStartSign");
        if(CommonData.aMapLocation==null) return false;
        boolean in;
        LineStation departStation = holderTrip.getTrip().getDepartStation();
        in = GpsTool.checkPointInPolygon(
                CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude(),
                departStation.getLngLat(),
                departStation.getAreaRadius());
        //判断出发

        boolean isOut = !in && holderTrip.isStartIn();
        if (!holderTrip.isStartIn()) {
            holderTrip.setStartIn(in);
        }
        if (holderTrip.isStartIn() && !holderTrip.getTrip().isRoll()) {
            //只要进入范围，并且时间在范围内，打卡
            int durationWithNow = TimeTool.getDistanceTimesSign(TimeTool.getCurrentTime("HH:mm"), holderTrip.getTrip().getSchedule());
            if (durationWithNow >=Constants.DEPART_TIME_LEFT && durationWithNow <= Constants.DEPART_TIME_RIGHT) {
                //如果时间地点满足，打卡
                holderTrip.setStartSignPosition(UtilTool.getCurrentLatLng());
                //开始精确定位
                sign(holderTrip, Constants.STATUS_DEPART, null, null, mode);
                return in;
            }
        }
        if (isOut) {
            holderTrip.setStartSignPosition(UtilTool.getCurrentLatLng());
            //开始精确定位
            sign(holderTrip, Constants.STATUS_DEPART, null, null, mode);
        }
        return in;
    }

    public static boolean driveEndSign(HolderTrip holderTrip, PositionVo positionVo, int mode) {
        T.log("driveEndSign");
        boolean in = false;
        if(CommonData.aMapLocation==null) return false;
        LineStation arriveStation = holderTrip.getTrip().getArriveStation();
        List<LineStation> stations = holderTrip.getTrip().getBackupStations();
        if (stations == null) {
            stations = new ArrayList<>();
        }
        stations.add(arriveStation);
        Integer signArriveId = null;
        String arriveStationName = null;

        for (LineStation ls : stations) {
            in = GpsTool.checkPointInPolygon(CommonData.aMapLocation.getLongitude() + ";" + CommonData.aMapLocation.getLatitude(), ls.getLngLat(), arriveStation.getAreaRadius());
            if (in) {
                signArriveId = ls.getId();
                arriveStationName = ls.getName();
                break;
            }
        }
        if (in) {
            holderTrip.setEndSignPosition(UtilTool.getCurrentLatLng());
//            new TraceTool().startTrace();
            sign(holderTrip, Constants.STATUS_ARRIVE, signArriveId, arriveStationName, mode);
            //上传完成轨迹的txt文档
//            RouteUpload.uploadFileType(RouteUpload.UPLOAD_TXT);
        }
        return in;
    }

    public static void sign(HolderTrip holderTrip, int signType,
                            Integer signStationId, String signStationName, int mode) {
        sign(holderTrip, null, signType, signStationId, null, signStationName, mode);
    }

    public static void sign(final HolderTrip holderTrip, final Integer tripId, final int signType,
                            final Integer signStationId, String lngLat,
                            final String signStationName, final int mode) {
        final Map<String, Object> param = new HashMap<>(20);
        if (tripId == null) {
            if(holderTrip!=null){
                param.put("tripId", holderTrip.getTrip().getId());
            }
        } else {
            if(tripId>0){
                param.put("tripId", tripId);
            }else {
                T.log("班次错误");
                return;
            }
        }
        if(holderTrip!=null){
            param.put("time",holderTrip.getTrip().getSchedule());
        }else {
            T.log("离线打卡上传");
        }
        if (signStationId != null) {
            param.put("signArriveId", signStationId);
        }
        param.put("status", signType);
        if (lngLat == null) {
            lngLat = UtilTool.getCurrentLatLng();
            if (!TextUtils.isEmpty(lngLat)) {
                param.put("lngLat", lngLat);
            }
        } else {
            param.put("lngLat", lngLat);
        }
        param.put("mode", mode);
        //回调函数
        CallBack callBack = new SimpleCallBack() {
            @Override
            public void success(int code, String message, String data) {
                if (code == 0 || code == 3) {
                    if (holderTrip == null) {
                        //清除本地记录
                        UtilTool.getDbManager().delTripFail(tripId, signType);
                        T.log("离线打卡成功: tripId = " + tripId + ", status = " + signType);

                        RxBus.getInstance().post(new UpdateEvent(true));
                        UtilTool.getInstance().getSchedules();

                    } else {
                        String playStr;
                        if (Constants.STATUS_DEPART == signType && !holderTrip.isStartOut()) {
                            //起点重复打卡
                            TaskDetail taskDetail = holderTrip.getTrip();
                            playStr = taskDetail.getDepartStation().getName() +
                                    T.getStringById(R.string.start_sign_success) + ",目的站点" +
                                    taskDetail.getArriveStation().getName();
                            if (mode == Constants.MODE_MAN_ABNORMAL) {
                                playStr = T.getStringById(R.string.error_sign);
                                holderTrip.setStartSignPosition(UtilTool.getCurrentLatLng());
                            }
                            holderTrip.getTrip().setStatus(Constants.STATUS_DEPART);
                            XmPlayer.getInstance().playTTS(playStr);
                            holderTrip.setStartOut(true);
                            refreshSignMarker(signType);
                        } else {
                            //终点
                            if (code == 0) {
                                String ttsStationName = signStationName == null ?
                                        holderTrip.getTrip().getArriveStation().getName() : signStationName;
                                playStr = ttsStationName + T.getStringById(R.string.end_sign_success);
                                if (mode == Constants.MODE_MAN_ABNORMAL) {
                                    playStr = T.getStringById(R.string.error_sign);
                                    holderTrip.setEndSignPosition(UtilTool.getCurrentLatLng());
//                                    new TraceTool().startTrace();
//                                    RouteUpload.uploadFileType(RouteUpload.UPLOAD_TXT);
                                }
                                XmPlayer.getInstance().playTTS(playStr);
                            }
                            //TODO
                            refreshSignMarker(signType);
                            //将打卡班次设置为空
                            CommonData.holderTrip = null;
                            //刷新首页列表
                        }
//                        UtilTool.refreshTodayTask();
                        RxBus.getInstance().post(new UpdateEvent(true));
                        UtilTool.getInstance().getSchedules();
                        //刷新首页列表
                    }
                }
            }

            @Override
            public void error(String responseMessage) {
                //Map加入Share，保存下来，默认只有一班未打起才能处理
                T.log("无网络等待上传打卡信息");
                TripFailBean bean = new TripFailBean();
                if(holderTrip!=null){
                    bean.setTripId(Integer.valueOf(holderTrip.getTrip().getId()));
                }
                bean.setStatus(signType);
                bean.setMode(mode);
                bean.setLngLat((String) param.get("lngLat"));
                bean.setSignArriveId(signStationId);
                bean.setTime(System.currentTimeMillis());
                boolean isExist = UtilTool.getDbManager().tripFailExist(bean.getId());
                if (isExist){
                    T.log("isExist:  "+isExist);
                    T.log("signType:  "+signType);

                    if (signType == Constants.STATUS_DEPART) {
                        //出发打卡
                        if(holderTrip!=null){
                            holderTrip.setStartOut(true);
                            holderTrip.getTrip().setStatus(Constants.STATUS_DEPART);
                        }
                        XmPlayer.getInstance().playTTS("离线出发打卡成功");
                    } else {
                        //到达
                        if(holderTrip!=null){
                            holderTrip.getTrip().setStatus(Constants.STATUS_ARRIVE);
                        }
                        CommonData.holderTrip = null;
                        XmPlayer.getInstance().playTTS("离线到达打卡成功");
                    }
                    UtilTool.changeHomeUi();
                    refreshSignMarker(signType);

                }else if (UtilTool.getDbManager().insertTripFail(bean)) {
                    //修改状态
                    if (signType == Constants.STATUS_DEPART) {
                        //出发打卡
                        if(holderTrip!=null){
                            holderTrip.setStartOut(true);
                            holderTrip.getTrip().setStatus(Constants.STATUS_DEPART);
                        }
                        XmPlayer.getInstance().playTTS("离线出发打卡成功");
                    } else {
                        //到达
                        if(holderTrip!=null){
                            holderTrip.getTrip().setStatus(Constants.STATUS_ARRIVE);
                        }
                        CommonData.holderTrip = null;
                        XmPlayer.getInstance().playTTS("离线到达打卡成功");
                    }
                    UtilTool.changeHomeUi();

                    refreshSignMarker(signType);
                }
                T.log("没有网络，上传失败");
            }
        };
        //手动打卡在UI线程，需要异步打卡，自动打卡在自定义线程，同步打卡
        if (mode == Constants.MODE_AUTO) {
            WebService.doSign(param, callBack);
//            WebService.executeRequestSync(WebService.POST, WebInterface.DRIVER_SIGN, param, callBack);
        } else {
            WebService.doSign(param, callBack);
//            WebService.doRequest(WebService.POST, WebInterface.DRIVER_SIGN, param, callBack);
        }
    }

    /**
     * 刷新打卡标记
     */
    private static void refreshSignMarker(int signCode) {
        if (signCode == 1) {
            if (RunMapActivity.getInstance() != null && CommonData.holderTrip != null) {
                RunMapActivity.getInstance().addStartSignMarker();
            }
        } else if (signCode == 2) {
            if (RunMapActivity.getInstance() != null) {
                RunMapActivity.getInstance().addEndSignMarker();
            }
        }
    }

    private boolean allIsRoll(List<TaskDetail> list){
        for(TaskDetail taskDetail:list){
            if(!taskDetail.isRoll()){
                return false;
            }
        }
        return true;
    }

    private boolean isIn(List<TaskDetail> list){
        for(TaskDetail taskDetail:list){
            if(GpsTool.checkPointInPolygon(
                    CommonData.aMapLocation.getLatitude(),
                    CommonData.aMapLocation.getLongitude(),
                    taskDetail.getDepartStation().getLngLat(),
                    taskDetail.getDepartStation().getAreaRadius())){
                return true;
            }
        }
        return false;
    }

//处理异常班次
    private void checkSchedule(List<TaskDetail> list){
        if(list==null||list.size()==0){
            return;
        }
        List<TaskDetail> goingList=new ArrayList<>();
        for(TaskDetail taskDetail:list){
            if(taskDetail.getStatus()==Constants.STATUS_DEPART){
                goingList.add(taskDetail);
            }
        }
        if(goingList.size()>=2){
            int [] temps=new int[goingList.size()];
            Map<Integer,Integer> map=new HashMap<>();
            int dis=0;
            for(int i=0;i<goingList.size();i++){
                String scheduleTime = goingList.get(i).getSchedule();
                dis=TimeTool.getDistanceTimes(scheduleTime, TimeTool.getCurrentTime("HH:mm"));
                map.put(dis,i);
                temps[i]=dis;
            }
            Arrays.sort(temps);
            Integer key=map.get(temps[0]);
            goingList.remove(goingList.get(key));
            for(TaskDetail taskDetail:goingList){
                sign(taskDetail.toHolderTrip(), Constants.STATUS_ARRIVE, null, null, Constants.MODE_MAN_ABNORMAL);
            }
        }
    }

}
