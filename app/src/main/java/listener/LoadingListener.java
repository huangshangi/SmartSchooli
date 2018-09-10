package listener;

import java.util.List;

import bean.Shuoshuo_Bean;

public interface LoadingListener {

    //获取到说说回调该方法
    public void loadingDown(List<Shuoshuo_Bean> list);
}
