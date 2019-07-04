package com.meibanlu.driver.tool.web;

import com.meibanlu.driver.webservice.response.ResponseEnvelope;

/**
 * 回调函数
 */
public interface CallBack {

    /**
     * 成功回调
     *
     * @param code    请求标志码
     * @param message 请求返回信息
     * @param data    请求返回数据
     */
    void success(int code, String message, String data);

    /**
     * 重载成功回到
     *
     * @param result 回调字符串
     */
    void success(String result);


    void error(String responseMessage);
}
