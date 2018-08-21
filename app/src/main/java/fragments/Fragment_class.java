package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.Fragment_class_listView_adapter;
import adapter.ScrollAdapter;
import bean.Class_Bean;
import utils.MyApplication;

//课堂界面
public class Fragment_class extends Fragment {

    View view;

    String week;//课程周数

    List<Class_Bean> list;

    List<TextView>list_textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_class_layout,container,false);

        initViews();
        ListView listView=(ListView)view.findViewById(R.id.fragment_class_listView);
        listView.setAdapter(new Fragment_class_listView_adapter(getActivity()));
        listView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        refreshData();

        return view;
    }

    //组装7*12个TextView,将其传递到ScrollAdapter
    private List<TextView> getTextViews(){
        list_textView=new ArrayList<TextView>();
        for(int i=0;i<7*12;i++){
            TextView textView=new TextView(MyApplication.getContext());
        }
        return null;
    }

    private void  refreshData(){
        //模拟从数据库获取数据
        Class_Bean bean=new Class_Bean();
        bean.setAddress("3区402");
        bean.setDay(7);
        bean.setFrom(1);
        bean.setTo(4);
        bean.setName("大计基");
        bean.setTeacher("程登峰");
        bean.setType("no");
        bean.setWeekfrom(1);
        bean.setWeekto(12);
        list.add(bean);
        new ScrollAdapter(list,view);
    }

    private void initViews(){
        list=new ArrayList<>();


//        for()
    }


}
