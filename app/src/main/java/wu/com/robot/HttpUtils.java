package wu.com.robot;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by Administrator on 2018/10/11.
 */
public class HttpUtils {
    // 图灵机器人使用api的公网地址
    public static final String URL = "http://www.tuling123.com/openapi/api";
    // 申请的apiKey
    public static final String API_KEY = "745687c154bc4640822854efc93ed4f2";
    public static final String userId = "123";
    /**
     * 发送一个消息并且得到返回的消息
     */
    public static String  sendMessage(String mess) {
        //Msg msg = new Msg();
        String news;
        String uri = setParams(mess);
        String res = doGet(uri);

        Gson gson = new Gson();
        Result result = gson.fromJson(res, Result.class);
        if (result.getCode() > 400000 || result.getText() == null || result.getText().trim().equals("")) {
            news = "该功能等待开发...";
        //    msg.setContent("该功能等待开发...");
        } else {
            news = result.getText();
         //   msg.setContent(result.getText());
        }
        //msg.setType(Msg.TYPE_RECIVER);
        //msg.setDate(new Date());
        //return msg;
        return news;
    }

    /**
     * 拼接Url	 * @param msg	 * @return
     */
    private static String setParams(String msg) {
        try {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return URL + "?key=" + API_KEY + "&info=" + msg+"&userid="+userId;
    }

    /**
     * Get请求，获得返回数据	 * @param urlStr	 * @return
     */
    private static String doGet(String urlStr) {
        java.net.URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else {
                return "错误";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "错误";
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                if (baos != null) baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
    }

}
