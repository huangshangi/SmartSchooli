package listener;

import bean.Message_Bean;

public interface UpMessage_Listener {
    public void MessageDown(Message_Bean bean);//用户向用户发送的信息成功上传至服务器
}
