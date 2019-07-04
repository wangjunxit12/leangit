package com.meibanlu.driver.bean;

import java.util.List;

/**
 * AlwaysSpotBean常跑点的数据
 * Created by lhq on 2017/9/15.
 */

public class AlwaysSpotBean {
    private boolean isShow;//是否显示
    private List<AlwaysSpot> data;//数据

    public AlwaysSpot gethh() {
        return new AlwaysSpot();
    }

    public List<AlwaysSpot> getData() {
        return data;
    }

    public void setData(List<AlwaysSpot> data) {
        this.data = data;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    public class AlwaysSpot {

        public boolean isChoose() {
            return isChoose;
        }

        public void setChoose(boolean choose) {
            isChoose = choose;
        }

        public String getStationId() {
            return stationId;
        }

        public void setStationId(String stationId) {
            this.stationId = stationId;
        }

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        private String stationName;//站点名字
        private String stationId;//id站点的id
        private boolean isChoose;//是否选中

    }
}
