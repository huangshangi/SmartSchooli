package bean;

import cn.bmob.v3.BmobObject;

public class UpdateMessage_Bean extends BmobObject{

    //用来储存最新消息
    String update_content;

    String update_type;

    String update_receiverId;

    String update_senderId;

    String imageUrl;//对方头像

    String update_receiver_name;//对方昵称

    String update_sender_name;//自己昵称

    boolean hasread;

    public boolean getHasread() {
        return hasread;
    }

    public void setHasread(boolean hasread) {
        this.hasread = hasread;
    }

    public String getUpdate_receiver_name() {
        return update_receiver_name;
    }

    public String getUpdate_sender_name() {
        return update_sender_name;
    }

    public void setUpdate_receiver_name(String update_receiver_name) {
        this.update_receiver_name = update_receiver_name;
    }

    public void setUpdate_sender_name(String update_sender_name) {
        this.update_sender_name = update_sender_name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUpdate_content() {
        return update_content;
    }

    public String getUpdate_receiverId() {
        return update_receiverId;
    }

    public String getUpdate_senderId() {
        return update_senderId;
    }

    public String getUpdate_type() {
        return update_type;
    }

    public void setUpdate_content(String update_content) {
        this.update_content = update_content;
    }

    public void setUpdate_receiverId(String update_receiverId) {
        this.update_receiverId = update_receiverId;
    }

    public void setUpdate_senderId(String update_senderId) {
        this.update_senderId = update_senderId;
    }

    public void setUpdate_type(String update_type) {
        this.update_type = update_type;
    }
}
