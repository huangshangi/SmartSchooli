package bean;

import java.io.Serializable;

//课程信息
public class Class_Bean implements Serializable{

    String cNO;//课程号

    String courseNumber;//课序号

    String name;//课程名称

    String address;//课程地点

    int from;//课程开始节数

    int to;//课程结束节数

    int weekfrom;//课程开始周数

    int  weekto;//课程结束周数

    String type;//单双周课程：no->不是单双周课程  dan->单数周才有该课程 shuang ->双数周有该课程

    String teacher;//教师

    int day;//该课程属于周几的课程


    public String getcNO() {
        return cNO;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setcNO(String cNO) {
        this.cNO = cNO;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getFrom() {
        return from;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getTo() {
        return to;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeekfrom() {
        return weekfrom;
    }

    public int getWeekto() {
        return weekto;
    }

    public void setWeekfrom(int weekfrom) {
        this.weekfrom = weekfrom;
    }

    @Override
    public String toString() {
        return "Class_Bean{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", weekfrom=" + weekfrom +
                ", weekto=" + weekto +
                ", type='" + type + '\'' +
                ", teacher='" + teacher + '\'' +
                ", day=" + day +
                '}';
    }

    public void setWeekto(int weekto) {
        this.weekto = weekto;
    }


}

