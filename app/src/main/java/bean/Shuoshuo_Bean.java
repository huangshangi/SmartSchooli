package bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Shuoshuo_Bean extends BmobObject implements Serializable{

    String publisher_id;

    String publisher_name;

    String publisher_url;

    String published_content;

    String published_urls;

    String published_commit;

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public String getPublished_commit() {
        return published_commit;
    }

    public String getPublished_content() {
        return published_content;
    }

    public String getPublished_urls() {
        return published_urls;
    }

    public String getPublisher_url() {
        return publisher_url;
    }

    public void setPublished_commit(String published_commit) {
        this.published_commit = published_commit;
    }

    public void setPublished_content(String published_content) {
        this.published_content = published_content;
    }

    public void setPublished_urls(String published_urls) {
        this.published_urls = published_urls;
    }

    public void setPublisher_url(String publisher_url) {
        this.publisher_url = publisher_url;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }
}
