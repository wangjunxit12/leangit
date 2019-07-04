package com.meibanlu.driver.bean;

import java.util.List;

/**
 * Created by lhq on 2017-11-23.
 */

public class RewardBean {
    private double monthSum;
    private List<Bonus> bonus;
    public double getMonthSum() {
        return monthSum;
    }

    public void setMonthSum(double monthSum) {
        this.monthSum = monthSum;
    }

    public List<Bonus> getBonus() {
        return bonus;
    }

    public void setBonus(List<Bonus> bonus) {
        this.bonus = bonus;
    }
}
