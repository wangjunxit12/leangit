package com.meibanlu.driver.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.meibanlu.driver.application.DriverApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * xmPlay
 * Created by lhq on 2016/10/11.
 */
public class XmPlayer implements SpeechSynthesizerListener {
    private final String TAG = "XM_LOG";
    @SuppressLint("StaticFieldLeak")
    private static XmPlayer xmPlayer;  //单例对象
    //百度TTS的参数；
    private SpeechSynthesizer mSpeechSynthesizer;

    /**
     * 参数
     */
    private String mSampleDirPath;
    private String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private String LICENSE_FILE_NAME = "temp_license";
    private String MODEL="bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
    private String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private Context context = DriverApplication.getApplication();//无敌操作

    //公共的方法
    private XmPlayer() {
        //初始化TTS和MP3
        initialEnv();
        initialTts();
    }

    /**
     * 创建 TtsAndPlayer对象
     *
     * @return TtsAndPlayer对象
     */
    public static XmPlayer getInstance() {
        if (xmPlayer == null) {
            xmPlayer = new XmPlayer();
        }
        return xmPlayer;
    }

    /**
     * @param str 播放内容；
     */
    public void playTTS(String str) {
        this.mSpeechSynthesizer.speak(str);
    }


    /**
     * tts完成播放
     *
     * @param arg0 arg0
     */
    @Override
    public void onSpeechFinish(String arg0) {
    }


    //百度tts的方法***********************************************************************************

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            String SAMPLE_DIR_NAME = "baiduTTS";
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, MODEL, mSampleDirPath + "/" + MODEL);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);

//        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
//                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
//        String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
//        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
//                + ENGLISH_SPEECH_MALE_MODEL_NAME);
//        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
//                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source  source
     * @param dest    dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialTts() {
        LoggerProxy.printable(true);
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(context);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + MODEL);

        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "12");
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("10261124");
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("5RyknsUCudG2PmVOBi79in30", "66b11af4aa714d04c14ef8fb3b6c4b38");
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
            Log.i("nima", "auth success");
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Log.e("nima", "auth failed errorMsg=" + errorMsg);
        }
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
//        mSpeechSynthesizer.loadModel(mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
//        int result = mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
//                + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
    }

    protected void StopTts() {
        Log.v(TAG, "tts播放停止");
        this.mSpeechSynthesizer.stop();
    }

    public void onDestroy() {
        Log.v(TAG, "tts销毁");
        this.mSpeechSynthesizer.stop();
        this.mSpeechSynthesizer.release();
    }

    @Override
    public void onError(String arg0, SpeechError arg1) {

    }

    @Override
    public void onSpeechProgressChanged(String arg0, int arg1) {

    }

    @Override
    public void onSpeechStart(String arg0) {

    }

    @Override
    public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {

    }

    @Override
    public void onSynthesizeFinish(String arg0) {

    }

    @Override
    public void onSynthesizeStart(String arg0) {

    }
    //百度tts的方法

}
