package com.meibanlu.driver.tts;

import android.util.Log;
import android.util.Pair;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.meibanlu.driver.application.DriverApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类是对SpeechSynthesizer的封装
 * <p>
 * Created by fujiayi on 2017/5/24.
 */

public class MySyntherizer implements MainHandlerConstant {

    protected SpeechSynthesizer mSpeechSynthesizer;

    private static final String TAG = "NonBlockSyntherizer";

    private static boolean isInitied = false;

    private boolean isCheckFile = true;

    private volatile static MySyntherizer instance;

    private String appId = "15964311";

    private String appKey = "P6xGGKCcHDcs0ig39uUHbG7Z";

    private String secretKey = "Zyit4bIA9RG2Ybx1sCMZYbbdhlXdTNdF";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    private String offlineVoice = OfflineResource.VOICE_FEMALE;




    public static MySyntherizer getInstance(){
        if(instance==null){
            instance=new MySyntherizer();
            synchronized (MySyntherizer.class){
                if(instance==null) {
                    instance = new MySyntherizer();
                }
            }
        }
        return instance;
    }


    public MySyntherizer() {
        if (isInitied) {
            // SpeechSynthesizer.getInstance() 不要连续调用
            throw new RuntimeException("MySynthesizer 类里面 SpeechSynthesizer还未释放，请勿新建一个新类");
        }
        init();
        isInitied = true;
    }

    /**
     * 注意该方法需要在新线程中调用。且该线程不能结束。详细请参见NonBlockSyntherizer的实现
     *
     *
     */
    private void init() {
        LoggerProxy.printable(true);
        sendToUiThread("初始化开始");
        Map<String, String> params = getParams();
        InitConfig config= new InitConfig(appId, appKey, secretKey, ttsMode, params, new MessageListener());
        boolean isMix = config.getTtsMode().equals(TtsMode.MIX);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(DriverApplication.getApplication());
        mSpeechSynthesizer.setSpeechSynthesizerListener(config.getListener());


        // 请替换为语音开发者平台上注册应用得到的App ID ,AppKey ，Secret Key ，填写在SynthActivity的开始位置
        mSpeechSynthesizer.setAppId(config.getAppId());
        mSpeechSynthesizer.setApiKey(config.getAppKey(), config.getSecretKey());

        if (isMix) {
            // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。选择纯在线可以不必调用auth方法。
            AuthInfo authInfo = mSpeechSynthesizer.auth(config.getTtsMode());
            if (!authInfo.isSuccess()) {
                // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
                String errorMsg = authInfo.getTtsError().getDetailMessage();
                sendToUiThread("鉴权失败 =" + errorMsg);
                return;
            } else {
                sendToUiThread("验证通过，离线正式授权文件存在。");
            }
        }
        setParams(config.getParams());
        // 初始化tts
        int result = mSpeechSynthesizer.initTts(config.getTtsMode());
        if (result != 0) {
            sendToUiThread("【error】initTts 初始化失败 + errorCode：" + result);
            return;
        }
        // 此时可以调用 speak和synthesize方法
        sendToUiThread(INIT_SUCCESS, "合成引擎初始化成功");
    }

    /**
     * 合成并播放
     *
     * @param text 小于1024 GBK字节，即512个汉字或者字母数字
     * @return
     */
    public int speak(String text) {
        Log.i(TAG, "speak text:" + text);
        return mSpeechSynthesizer.speak(text);
    }

    /**
     * 合成并播放
     *
     * @param text        小于1024 GBK字节，即512个汉字或者字母数字
     * @param utteranceId 用于listener的回调，默认"0"
     * @return
     */
    public int speak(String text, String utteranceId) {
        return mSpeechSynthesizer.speak(text, utteranceId);
    }

    /**
     * 只合成不播放
     *
     * @param text
     * @return
     */
    public int synthesize(String text) {
        return mSpeechSynthesizer.synthesize(text);
    }

    public int synthesize(String text, String utteranceId) {
        return mSpeechSynthesizer.synthesize(text, utteranceId);
    }

    public int batchSpeak(List<Pair<String, String>> texts) {
        List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
        for (Pair<String, String> pair : texts) {
            SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
            speechSynthesizeBag.setText(pair.first);
            if (pair.second != null) {
                speechSynthesizeBag.setUtteranceId(pair.second);
            }
            bags.add(speechSynthesizeBag);

        }
        return mSpeechSynthesizer.batchSpeak(bags);
    }

    public void setParams(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                mSpeechSynthesizer.setParam(e.getKey(), e.getValue());
            }
        }
    }

    public int pause() {
        return mSpeechSynthesizer.pause();
    }

    public int resume() {
        return mSpeechSynthesizer.resume();
    }

    public int stop() {
        return mSpeechSynthesizer.stop();
    }

    /**
     * 引擎在合成时该方法不能调用！！！
     * 注意 只有 TtsMode.MIX 才可以切换离线发音
     *
     * @return
     */
    public int loadModel(String modelFilename, String textFilename) {
        int res  = mSpeechSynthesizer.loadModel(modelFilename, textFilename);
        sendToUiThread("切换离线发音人成功。");
        return res;
    }

    /**
     * 设置播放音量，默认已经是最大声音
     * 0.0f为最小音量，1.0f为最大音量
     *
     * @param leftVolume  [0-1] 默认1.0f
     * @param rightVolume [0-1] 默认1.0f
     */
    public void setStereoVolume(float leftVolume, float rightVolume) {
        mSpeechSynthesizer.setStereoVolume(leftVolume, rightVolume);
    }

    public void release() {
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
        mSpeechSynthesizer = null;
        isInitied = false;
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "12");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }
    private OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(DriverApplication.getApplication(), voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
        }
        return offlineResource;
    }
    private void sendToUiThread(String message) {
        sendToUiThread(PRINT, message);
    }

    private void sendToUiThread(int action, String message) {
        Log.i(TAG, message);
    }

}
