package listener;

import java.util.List;

import bean.Message_Bean;

public interface Message_Down_Listener {

    //获取说说成功后调用该方法
    public void getMessageDown(List<Message_Bean> list);
}
