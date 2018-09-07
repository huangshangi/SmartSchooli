package bean;

import cn.bmob.v3.BmobObject;

public class Image_Bean extends BmobObject {

    String phone_id;//手机唯一标识符

    String image_sd_url;//文件在sd卡中地址

    String image_inet_url;//文件在网络上地址

    public void setPhone_id(String phone_id) {
        this.phone_id = phone_id;
    }

    public void setImage_sd_url(String image_sd_url) {
        this.image_sd_url = image_sd_url;
    }

    public void setImage_inet_url(String image_inet_url) {
        this.image_inet_url = image_inet_url;
    }

    public String getPhone_id() {
        return phone_id;
    }

    public String getImage_sd_url() {
        return image_sd_url;
    }

    public String getImage_inet_url() {
        return image_inet_url;
    }
}

