package listener;

import java.util.List;

import bean.Repair_Bean;

public interface LoadingRepairListener {

    //获取维修信息成功时回调该接口
    public void loadingDown(List<Repair_Bean> list);
}
