package listener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import bean.Class_Bean;

//获取课程信息成功后回调该方法
public interface Fragment_class_Listener {
    public void getClassDown(ArrayList<Class_Bean> list);
}
