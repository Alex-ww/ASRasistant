package wu.com.asrasistant;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import wu.com.WakeUp.WakeUtil;
import wu.com.asr.SpeackUtil;
import wu.com.robot.HttpUtils;
import wu.com.robot.Msg;
import wu.com.syn.AudioUtils;

public class MainActivity extends AppCompatActivity implements SpeackUtil.OnLineCallBack ,WakeUtil.WakeCallBack {
    private static final String TAG="MainActivity";
    private String voice;
    private TextView text1,text2,text3;
    private TextView btn;
    private SpeackUtil speackUtil;
    private AudioUtils audioUtils;
    private WakeUtil wakeUtil;
    private Button wake_btn;
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speackUtil = new SpeackUtil(MainActivity.this,this);
        audioUtils = AudioUtils.getInstance();
        audioUtils.initTTs(getApplicationContext());
        init();
        wakeUtil = new WakeUtil(this,this);
        wakeUtil.start();
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                speackUtil.start();
//
//            }
//        });
//          wake_btn.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                  Log.i(TAG,"唤醒按钮被点击");
//                  wakeUtil.start();
//              }
//          });

    }

    private void init() {
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        btn = findViewById(R.id.btn);
        //wake_btn = findViewById(R.id.wake_btn);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String from = (String) msg.obj;
            text3.setText(from);
            audioUtils.speak(text3.getText().toString());
        }
    };

    @Override
    public void onSuccess( String flag) {
        //text1.setText(result);
        text2.setText(flag);
        voice = text2.getText().toString();
        Toast.makeText(MainActivity.this,voice,Toast.LENGTH_SHORT).show();
        if(!"".equals(voice)) {
            new Thread() {
                public void run() {
                    String from = null;
                    try {
                        from = HttpUtils.sendMessage(voice);
                        Log.i(TAG, from);
                    } catch (Exception e) {
                        from = "服务器挂了呢...";
                    }

                    Log.i(TAG, from);
                    Message message = Message.obtain();
                    message.obj = from;
                    mHandler.sendMessage(message);
                }
            }.start();
        }else{  }
    }

    @Override
    public void onProcess(String process) {
        text1.setText(process);
    }

    @Override
    public void WakeSuccess(String s) {
        btn.setText(s);
        audioUtils.speak("在呢");
        speackUtil.start();
    }

    @Override
    public void WakeFail() {
        Toast.makeText(this,"唤醒失败",Toast.LENGTH_SHORT).show();
    }
}
