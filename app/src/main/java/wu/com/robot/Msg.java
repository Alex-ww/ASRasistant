package wu.com.robot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/10/11.
 */
public class Msg {
    public static final int TYPE_RECIVER=0;
    public static final int TYPE_SEND=1;

    private String content;
    private int type;
    /**
     * 日期
     */
    private Date date;
    /**
     * 日期的字符串格式
     */
    private String dateStr;
    /**
     * 发送人
     */
    private String name;


    public Msg(String content, int type){
        this.content=content;
        this.type=type;
        setDate(new Date());
    }
    public Msg(){

    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
    public String getDateStr() {
        return dateStr;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateStr = df.format(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
