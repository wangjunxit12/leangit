package com.meibanlu.driver.tool.web;

/*
 * 网络请求发送服务类
 * Created by leigang on 2016/11/23.
 */

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.meibanlu.driver.R;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.tool.Constants;
import com.meibanlu.driver.tool.NetManager;
import com.meibanlu.driver.tool.T;
import com.meibanlu.driver.tool.TimeTool;
import com.meibanlu.driver.webservice.RetrofitGenerator;
import com.meibanlu.driver.webservice.requeset.ArriveRequest;
import com.meibanlu.driver.webservice.requeset.Header;
import com.meibanlu.driver.webservice.requeset.RequestBody;
import com.meibanlu.driver.webservice.requeset.RequestEnvelope;
import com.meibanlu.driver.webservice.requeset.StartOffRequest;
import com.meibanlu.driver.webservice.response.ResponseEnvelope;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 网络请求处理工具
 */
public class WebService {
    //请求方法
    public static final int GET = 0x0;

    public static final int POST = 0x1;

    private static final String CHARSET = "utf-8";

//    private static Handler handler = new Handler();

//    /**
//     * 线程池，发送请求使用，初始化3个线程
//     */
//    private static ExecutorService webServicePool = newFixedThreadPool(3);

    @NonNull
    private CompositeDisposable mCompositeDisposable=new CompositeDisposable();


    private volatile static WebService instance;

    private WebService(){

    }

    public static WebService getInstance(){
        if(instance==null){
            instance=new WebService();
        }
        return instance;
    }

    /**
     * 生成远程访问的地址
     *
     * @param param 参数列表，按照顺序依次替换url字符串中的参数
     * @return return
     */
    private static String getWebServiceUrl(WebInterface webInterface, String... param) {
//      定义IP地址
//        String urlPrefix = "http://182.140.132.153:8080/";  // 雷刚电脑
        String urlPrefix = "https://www.meibanlu.com/";  // 阿里云
        String url = webInterface.getUrl();
        //替换参数
        String paramPrefix = "PARAM";
        for (int i = 0; i < param.length; i++) {
            if (param[i] == null) {
                return null;
            }
            url = url.replace(paramPrefix + i, param[i]);
        }
        return urlPrefix + url;
    }


    public  void getData(RequestEnvelope envelope,String soap,final DataCallBack callBack){
            if (!NetManager.connect()) {
                DriverApplication.getApplication().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.showShort(T.getStringById(R.string.network_error));
                    }
                });
                return;
            }
        mCompositeDisposable.add(RetrofitGenerator.getInstance().getApiStore().getData(envelope,soap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseEnvelope>() {
                    @Override
                    public void accept(ResponseEnvelope responseEnvelope) throws Exception {
                          callBack.success(responseEnvelope);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(throwable.getMessage()!=null){
                            Log.e("WebService",throwable.getMessage());
                        }
                        callBack.error(throwable.getMessage());
                    }
                }));

    }


    public static void doSign(Map<String, Object> param, final CallBack callBack){

        RequestEnvelope envelope=new RequestEnvelope();
        RequestBody body=new RequestBody();
        Header header=new Header();
        envelope.header=header;
        final int status= (Integer) param.get("status");
        String id=String.valueOf(param.get("tripId")) ;
        String lnglat=(String) param.get("lngLat");
        int mode=(Integer) param.get("mode");

        String urn;
        if(status==1){
            StartOffRequest request=new StartOffRequest();
            request.setTime(TimeTool.getCurrentTime("yyyy-MM-dd HH:mm"));
            request.setLineNumber(id);
            request.setLnglat(lnglat);
            if(mode== Constants.MODE_MAN_ABNORMAL){
                request.setStatus("0");
            }else {
                request.setStatus("1");
            }

            body.offRequest=request;
            envelope.body=body;
            urn="urn:TYWJAPPIntf-ITYWJAPP#sijichufa";
        }else {
            ArriveRequest request=new ArriveRequest();
            request.setTime(TimeTool.getCurrentTime("yyyy-MM-dd HH:mm"));
            request.setLineNumber(id);
            request.setLnglat(lnglat);
            if(mode== Constants.MODE_MAN_ABNORMAL){
                request.setStatus("0");
            }else {
                request.setStatus("1");
            }
            body.arriveRequest=request;
            envelope.body=body;
            urn="urn:TYWJAPPIntf-ITYWJAPP#sijishouche";
        }
        RetrofitGenerator.getInstance().getApiStore().getData(envelope,urn)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<ResponseEnvelope>() {
            @Override
            public void accept(ResponseEnvelope responseEnvelope) throws Exception {
                 String data;
                if(status==1){
                     data=responseEnvelope.body.offResponse.model.value;
                 }else {
                    data=responseEnvelope.body.arriveResponse.model.value;
                }
                if(data.equalsIgnoreCase("ok")){
                    callBack.success(0,"","");
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if(throwable.getMessage()!=null) {
                    callBack.error(throwable.getMessage());
                    T.logE(throwable.getMessage());
                }

            }
        });

    }
}
