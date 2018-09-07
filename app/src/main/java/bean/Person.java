package bean;

public class Person {
    public static String publisher;
    public static String publisher_image="http://bmob-cdn-20795.b0.upaiyun.com/2018/08/05/506a139d40752c99806d999f6b503d5e.jpg";
    public static String id;


    public static void setPublisher(String publisher) {
        Person.publisher = publisher;
    }

    public static String getPublisher() {
        return publisher;
    }

    public static void setPublisher_image(String publisher_image) {
        Person.publisher_image = publisher_image;
    }

    public static String getPublisher_image() {
        return publisher_image;
    }

    public static void setId(String id) {
        Person.id = id;
    }

    public static String getId() {
        return id;
    }
}
