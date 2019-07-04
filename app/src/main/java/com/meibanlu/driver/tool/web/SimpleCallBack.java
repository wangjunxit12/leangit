package com.meibanlu.driver.tool.web;

/**
 * SimpleCallBack 处理的简单的CallBack
 * Created by lhq on 2017/8/28.
 */

public abstract class SimpleCallBack implements CallBack {
    /**
     * 重载成功回到
     *
     * @param result 回调字符串
     */
    @Override
    public void success(String result) {
    }
    @Override
    public void error(String responseMessage) {
    }

}
