package bean;

import cn.bmob.v3.BmobObject;

public class Qiandao_Bean extends BmobObject{

    String name;//签到者姓名

    String xuehao;//签到学号

    String cNO;//课程号

    String courseNumber;//课序号

    String week;//签到的是那一周的课程

    String day;//签到的是哪一天的课程

    String jieshu;//哪一节课程

    public String getJieshu() {
        return jieshu;
    }

    public void setJieshu(String jieshu) {
        this.jieshu = jieshu;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public void setcNO(String cNO) {
        this.cNO = cNO;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public String getcNO() {
        return cNO;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDay() {
        return day;
    }

    public String getWeek() {
        return week;
    }

    public String getXuehao() {
        return xuehao;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setXuehao(String xuehao) {
        this.xuehao = xuehao;
    }


}
