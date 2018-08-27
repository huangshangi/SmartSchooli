package listener;

import java.util.List;

import bean.Class_Bean;

//获取课程信息成功后回调该方法
public interface Fragment_class_Listener {
    public void getClassDown(List<Class_Bean> list);
}
