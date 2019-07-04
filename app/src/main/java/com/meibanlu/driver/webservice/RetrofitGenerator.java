package com.meibanlu.driver.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meibanlu.driver.BuildConfig;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitGenerator {
    private static final String SMS_URL="http://sms.cd917.com/api/SmsService/";
    private static final String TEST_URL="http://120.79.206.107:9999/soap/";
    private final static String BASE_URL = "http://110.185.107.182:9999/soap/";
    private static volatile RetrofitGenerator generator;
    private ApiStore apiStore;
    private final static int CONNECT_TIMEOUT = 10;

    private final static int READ_TIMEOUT = 20;

    private final static int WRITE_TIMEOUT = 10;

    private RetrofitGenerator() {

    }

    public static RetrofitGenerator getInstance() {
        if (generator == null) {
            synchronized (RetrofitGenerator.class) {
                if (generator == null) {
                    generator = new RetrofitGenerator();
                    return generator;
                }
            }
        }
        return generator;
    }

    private OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        File file = new File(app.getFilesDir(), "retrofit");
        int size = 32 * 1024 * 1024;
//        Cache cache = new Cache(file, size);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                ;
        return builder.build();

    }

    private Retrofit getRetrofit() {
        Strategy strategy = new AnnotationStrategy();

        Serializer serializer = new Persister(strategy);
        String url=null;
        if(BuildConfig.DEBUG){
            url=TEST_URL;
        }else {
            url=BASE_URL;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .baseUrl(url)
                .client(getClient())
                .build();
        return retrofit;

    }


    private  <S> S createService(Class<S> sClass) {
        return getRetrofit().create(sClass);
    }
    public ApiStore getApiStore(){
        if (apiStore==null){
            apiStore=createService(ApiStore.class);
        }
        return apiStore;
    }
    public ApiStore getApiStore(String url){
        Gson gson = new GsonBuilder()
                //配置你的Gson
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
        return new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url).client(getClient()).build().create(ApiStore.class);
    }
}
