package com.meibanlu.driver.bean;

import java.util.List;

/**
 * MileageBean汽车里程
 * Created by lhq on 2018-01-08.
 */

public class MileageBean {
    public List<Mileage> getMileageLogs() {
        return mileageLogs;
    }

    public void setMileageLogs(List<Mileage> mileageLogs) {
        this.mileageLogs = mileageLogs;
    }


    public List<String> getCarNumbers() {
        return carNumbers;
    }

    public void setCarNumbers(List<String> carNumbers) {
        this.carNumbers = carNumbers;
    }

    private List<Mileage> mileageLogs;
    private List<String> carNumbers;

}
