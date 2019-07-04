package com.meibanlu.driver;



public interface OnPollingListener extends BaseManagerInterface{
    int DELAY = 5000;
    /**
     * Called after at least {@link #DELAY} milliseconds.
     */
    void onTimer();
}
