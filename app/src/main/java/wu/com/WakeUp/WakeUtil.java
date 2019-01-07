package wu.com.WakeUp;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;


public class WakeUtil{
    private final static String TAG = "WakeUtil";
    private EventManager em;
    private Context context;
    private WakeCallBack wakeCallBack;

    public WakeUtil(Context context,WakeCallBack wakeCallBack){
        this.context =context;
        this.wakeCallBack = wakeCallBack;

        em = EventManagerFactory.create(context,"wp");
        em.registerListener(myEventListener);
    }

    public void start(){
// 基于SDK唤醒词集成第2.1 设置唤醒的输入参数
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
       em.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }

    public void stop(){
        em.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }

    private void success(String s){
       wakeCallBack.WakeSuccess(s);
    }

    private void fail(){
        wakeCallBack.WakeFail();
    }




    private EventListener myEventListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] bytes, int i, int i1) {
            Log.i(TAG, String.format("event: name=%s, params=%s", name, params));
                  //唤醒事件
            if (name.equals("wp.data")) {
                try {
                    JSONObject json = new JSONObject(params);
                    int errorCode = json.getInt("errorCode");
                    String word = json.getString("word");
                    if (errorCode == 0) {
                     success(word);//唤醒成功
                    } else {
                     fail();   //唤醒失败
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ("wp.exit".equals(name)) {
              //唤醒已停止
            }
        }
    };

    public interface WakeCallBack{
        void WakeSuccess(String s);
        void WakeFail();
    }

}
