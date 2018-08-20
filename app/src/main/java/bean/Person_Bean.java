package bean;


import cn.bmob.v3.BmobUser;

//用户个人信息
public class Person_Bean extends BmobUser{

    String nickname;

    String image;//储存头像图片的url

    String kind;//人员种类

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
