package listener;

import java.util.List;

import bean.Message_Bean;

public interface MessageUpdateListener {
    //有新消息时回调该方法
    public void haveMessage(List<Message_Bean> list);
}
