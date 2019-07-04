package com.meibanlu.driver.tool;

import com.amap.api.location.AMapLocation;
import com.meibanlu.driver.bean.PositionVo;

import java.util.LinkedList;

/**
 * 位置纠偏算法
 *
 * @author Administrator
 */
public class RectifyLocationUtils {

    /**
     * 300km/h   值为   m/s
     */
    private final static double MAX_SPEED = 130 / 3.6;

    private final static int VO_SIZE = 5;

    /**
     * 连续出现异常点的次数上限，达到上限就将异常点修改为正常点
     */
    private final static int ABNORMAL_THRESHOLD = 3;

    private static LinkedList<PositionVo> positions = new LinkedList<>();

    /**
     * 传入一个位置，返回一个应该存入本地的位置
     *
     * @param aMapLocation 位置
     * @return 合理的位置
     */
    public synchronized static PositionVo rectify(AMapLocation aMapLocation, long time) {
        //构建对象
        PositionVo position = new PositionVo();
        position.setEnterTime(time);
        position.setLongitude(aMapLocation.getLongitude());
        position.setLatitude(aMapLocation.getLatitude());
        position.setBearing(aMapLocation.getBearing());
        if (positions.size() == 0) {
            position.setAbnormal(false);
            positions.addLast(position);
            return position;
        }
        //向前寻找
        int abnormal = 0;
        PositionVo selectPosition = null;
        for (int i = positions.size() - 1; i >= 0; i--) {
            //如果连续出现异常的次数达到上限，退出循环
            if (abnormal >= ABNORMAL_THRESHOLD) {
                break;
            }
            PositionVo loop = positions.get(i);
            if (loop.isAbnormal()) {
                //异常点
                abnormal++;
            } else {
                //与正常点比较，计算速度
                double speed = position.calSpeed(loop);
                T.log("速度 :" + speed);
                if (speed < MAX_SPEED) {
                    position.setAbnormal(false);
                    //属于正常, 返回当前点
                    selectPosition = position;
                } else {
                    //和正常点比较出现异常, 返回循环的当前点，设置当前为abnormal
                    position.setAbnormal(true);
                    selectPosition = loop;
                }
                break;
            }
        }
        if (selectPosition == null) {
            //如果没有选择到位置，则翻转最后一个异常点
            PositionVo last = positions.getLast();
            last.setAbnormal(false);
            //与正常点比较，计算速度
            double speed = position.calSpeed(last);
            if (speed < MAX_SPEED) {
                position.setAbnormal(false);
                //属于正常, 返回当前点
                selectPosition = position;
            } else {
                //和正常点比较出现异常, 返回循环的当前点，设置当前为abnormal
                position.setAbnormal(true);
                selectPosition = last;
            }
        }
        //添加点
        positions.addLast(position);
        //如果长度达到上限，剔除第一个点
        if (positions.size() > VO_SIZE) {
            positions.pollFirst();
        }
        //返回对象
//        aMapLocation.setLatitude(selectPosition.latitude);
//        aMapLocation.setLongitude(selectPosition.longitude);
//        aMapLocation.setTime(selectPosition.enterTime);
        return selectPosition;
    }

    public synchronized static void clearPosition() {
        positions.clear();
    }


}
