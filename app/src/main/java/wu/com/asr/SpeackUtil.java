package wu.com.asr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpeackUtil implements EventListener {
        private static final String TAG = "SpeackUtil";
        private EventManager asr;
        private OnLineCallBack onLineCallBack;
        private String final_result;
        private String flag;
        private String part_result ;

        public SpeackUtil (Context context, OnLineCallBack onLineCallBack){
            asr = EventManagerFactory.create(context,"asr");
            asr.registerListener(this);

            this.onLineCallBack = onLineCallBack;
            initPermission(context);
        }

        /*
         * android 6.0 以上需要动态申请权限
         */
        private void initPermission(Context context) {
            String permissions[] = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE
            };

            ArrayList<String> toApplyList = new ArrayList<String>();

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                    toApplyList.add(perm);
                    // 进入到这里代表没有权限.

                }
            }
            String tmpList[] = new String[toApplyList.size()];
            if (!toApplyList.isEmpty()) {
                ActivityCompat.requestPermissions((Activity) context, toApplyList.toArray(tmpList), 123);
            }

        }

    /**
     * 接口EventListener的实现方法，通过实现这个方法，可以得到语音处理后的回传数据
     * @param name 表示当前语音识别的状态
     * @param params  封装了语音处理后得到的结果的Json文件
     * @param data    PCM音频片段回调，必须输入ACCEPT_AUDIO_DATA 参数激活
     * @param offset  开始的地方
     * @param length  长度
     */
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
//            if (params != null && !params.isEmpty()) {
//
//                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(params);
//                        String resultType = jsonObject.getString("result_type");
//                        if (resultType.equals("final_result")){
//                            String finalResult = jsonObject.getString("best_result");
//
//                            onLineCallBack.onSuccess(finalResult);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
            String result = "";

            if (length > 0 && data.length > 0) {
                //result += ", 语义解析结果：" + new String(data, offset, length);
            }

            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎准备就绪，可以开始说话
                result += "引擎准备就绪，可以开始说话";

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户的已经开始说话
                result += "检测到用户的已经开始说话";

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                // 检测到用户的已经停止说话
                result += "检测到用户的已经停止说话";
                if (params != null && !params.isEmpty()) {
                    //result += "params :" + params + "\n";
                }
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                // 临时识别结果, 长语音模式需要从此消息中取出结果
                result += "识别临时识别结果:";
                if (params != null && !params.isEmpty()) {
                    result += parseAsrPartialJsonData(params);
                    //result += "params :" + params + "\n";
                }
//            Log.d(TAG, "Temp Params:"+params);
                //parseAsrPartialJsonData(params);
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束， 最终识别结果或可能的错误
                result += "识别结束";
               // btnStartRecord.setEnabled(true);
                asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
                if (params != null && !params.isEmpty()) {
                    //result +=parseAsrPartialJsonData(params);
                    result += "params :" + params + "\n";
                }
                Log.d(TAG, "Result Params:"+params);
                flag = parseAsrFinishJsonData(params);
                printResult(flag);
            }
            printProces(result );
        }

    private void printProces(String process) {
            onLineCallBack.onProcess(process);
    }

    public interface OnLineCallBack{
            void onSuccess(String flag);
            void onProcess(String process);
        }

        /**
         * 开始识别
         */
        public void start(){
            Map<String,Object> params = null;
            params = new LinkedHashMap<>();
            String event = null;
            event = SpeechConstant.ASR_START;

            params.put(SpeechConstant.PID, 1536); // 默认1536
            params.put(SpeechConstant.DECODER, 0); // 纯在线(默认)
            params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN); // 语音活动检测
            params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 2000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms
            params.put(SpeechConstant.ACCEPT_AUDIO_DATA, false);// 是否需要语音音频数据回调
            params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调
            String json = new JSONObject(params).toString();

            asr.send(event,json,null,0,0);
            //printResult("输入参数：" + json ,"");
        }

    private void printResult(String f) {
                onLineCallBack.onSuccess(f);

    }

    /**
         * 停止识别
         */
        public void stop() {
            asr.send(SpeechConstant.ASR_STOP,null,null,0,0);
        }
    /**
     *通过方法将回传的json文件的结果转化成自定义的类，并且通过自定义的类来接到想要的临时结果
     */
    private String parseAsrPartialJsonData(String data) {
        Log.d(TAG, "parseAsrPartialJsonData data:"+data);
        Gson gson = new Gson();
        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        Log.d(TAG, "resultType:"+resultType);
        if(resultType !=null && resultType.equals("partial_result")){
            part_result = jsonData.getBest_result();
        }
        if(resultType != null && resultType.equals("final_result")){
            final_result = jsonData.getBest_result();
           // Log.d(TAG,"临时结果:"+final_result);
//            tvParseResult.setText("解析结果：" + final_result);
        }
        return part_result;
    }

//    private String parseAsrPartJsonDate(String data){
//        Gson gson = new Gson();
//        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
//        String type = jsonData.getResult_type();
//        if(type !=null && type.equals("partial_result")){
//            part_result = jsonData.getBest_result();
//        }
//        return part_result;
//    }
    private String parseAsrFinishJsonData(String data) {
        Log.d(TAG, "parseAsrFinishJsonData data:"+data);
        Gson gson = new Gson();
        AsrFinishJsonData jsonData = gson.fromJson(data, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();
        if(desc !=null && desc.equals("Speech Recognize success.")){
            //tvParseResult.setText("解析结果:" + final_result);
            return  final_result;
        }else{
            String errorCode = "\n错误码:" + jsonData.getError();
            String errorSubCode = "\n错误子码:"+ jsonData.getSub_error();
            String errorResult = errorCode + errorSubCode;
            //tvParseResult.setText("解析错误,原因是:" + desc + "\n" + errorResult);
           // return "解析错误,原因是:" + desc + "\n" + errorResult ;
            return "";
        }
    }

}
