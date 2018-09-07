package listener;

import java.util.List;

import bean.UpdateMessage_Bean;

public interface UpdateMessageListener {

    //获取双方最新聊天记录的接口
    public void getUpdateMessage(List<UpdateMessage_Bean>list);

    //双方有新消息时
    public void getRedDotMessage();
}
