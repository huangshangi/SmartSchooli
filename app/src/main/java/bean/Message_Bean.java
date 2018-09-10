package bean;

import android.graphics.Bitmap;

import cn.bmob.v3.BmobObject;

/*
    用户发送消息的实体类
    当前属性：内容，用户是将该信息接受还是发出
    未来：英加入图片功能
 */
public class Message_Bean extends BmobObject{

    String content;
    String type;//消息类型
    String sender_id;//发送者id
    String receiver_id;//接受者id
    String image;//内部储存图像的url地址

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getType() {
        return type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
