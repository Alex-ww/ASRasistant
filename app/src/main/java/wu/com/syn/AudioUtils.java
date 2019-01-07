package wu.com.syn;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

public class AudioUtils {
    private static final String TAG= "AudioUtil";
    private static AudioUtils audioUtils;
    protected SpeechSynthesizer mSpeechSynthesizer;
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    private String appId = "15321445";

    private String appKey = "YYMrGhVw8rqNRfKXEOxfBja0";

    private String secretKey = "7AkhgWZtHizMiX2uzd9cgbTR3jStoTry";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.ONLINE;
    private AudioUtils(){

    }
    public static AudioUtils getInstance(){
        if (audioUtils == null){
            synchronized (AudioUtils.class){
                if (audioUtils == null){
                    audioUtils = new AudioUtils();
                }
            }
        }
        return audioUtils;
    }
    /**
     * 初始化语音识别
     */
    public void initTTs(Context context) {
//        boolean isMix = ttsMode.equals(TtsMode.MIX);
//        boolean isSuccess;
//        if (isMix) {
//            // 检查2个离线资源是否可读
//            isSuccess = checkOfflineResources();
//            if (!isSuccess) {
//                return;
//            } else {
////                print("离线资源存在并且可读, 目录：" + TEMP_DIR);
//            }
//        }
//        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler); // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(context);
        mSpeechSynthesizer.setSpeechSynthesizerListener(new myListen());    //通过设置监听来获取语音识别过程，以及可能出现的问题

        int result =  mSpeechSynthesizer.setAppId(appId);
        checkResult(result, "setAppId");                 //检查AppId是否正确
        result = mSpeechSynthesizer.setApiKey(appKey, secretKey);  //检查appKey, secretKey是否正确
        checkResult(result, "setApiKey");
//        if (isMix) {
//            // 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
//            isSuccess = checkAuth();
//            if (!isSuccess) {
//                return;
//            }
//            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME); // 文本模型文件路径 (离线引擎使用)
//            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME); // 声学模型文件路径 (离线引擎使用)
//        }

        // 以下setParam 参数，用来设置语音合成的一些值。不填写则默认值生效
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4"); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9"); // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");// 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "6");// 设置合成的语调，0-9 ，默认 5

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
        result = mSpeechSynthesizer.initTts(ttsMode);
        checkResult(result, "initTts");
    }
    private void checkResult(int result, String method) {     // 检查数据是否正确
        if (result != 0) {
            Log.i(TAG,"error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    public void speak(String text) {              //给定对应的字符串转化成语音
        int result = mSpeechSynthesizer.speak(text);
        checkResult(result, "speak");
    }
    public void stop() {                         //语音暂停
        int result = mSpeechSynthesizer.stop();
        checkResult(result, "stop");
    }
    public void onDestroy() {              //释放资源
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            Log.i(TAG,"释放资源成功");
        }
    }
    class myListen implements SpeechSynthesizerListener {              //接口判断合成过程
        @Override
        public void onSynthesizeStart(String s) {
            //合成准备工作
            Log.i(TAG,"合成准备");
        }
        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
            //合成数据和进度的回调接口，分多次回调
        }
        @Override
        public void onSynthesizeFinish(String s) {
               //合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
            Log.i(TAG,"合成结束");
        }
        @Override
        public void onSpeechStart(String s) {
            //播放开始，每句播放开始都会回调
            Log.i(TAG,"播放开始");
        }
        @Override
        public void onSpeechProgressChanged(String s, int i) {
            //播放进度回调接口，分多次回调
        }
        @Override
        public void onSpeechFinish(String s) {
            // 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
            Log.i(TAG,"播放结束");
        }
        @Override
        public void onError(String s, SpeechError speechError) {
            //当合成或者播放过程中出错时回调此接口
            Log.i(TAG,"合成或播放出错");
            Log.i(TAG,"错误码："+s+"错误内容："+(speechError.toString()));
        }
    }

}
