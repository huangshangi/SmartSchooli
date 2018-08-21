package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
//scrollView与listView滑动冲突暂未处理
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
        listView.setEnabled(false);
        refreshData();

        return view;
    }

    //组装7*12个TextView,将其传递到ScrollAdapter
    private List<TextView> getTextViews(){
        int width=getDisplayMetris()/8;
        list_textView=new ArrayList<TextView>();
        for(int i=0;i<7*12;i++){
            TextView textView=new TextView(MyApplication.getContext());
            textView.setBackgroundResource(R.drawable.fragment_class_tv_bg);
            textView.setWidth(width);
            textView.setHeight(width);
            list_textView.add(textView);
        }
        return list_textView;
    }

    private void  refreshData(){
        //模拟从数据库获取数据
        Class_Bean bean=new Class_Bean();
        bean.setAddress("3区402");
        bean.setDay(1);
        bean.setFrom(1);
        bean.setTo(2);
        bean.setName("大计基");
        bean.setTeacher("程登峰");
        bean.setType("no");
        bean.setWeekfrom(1);
        bean.setWeekto(12);
        list.add(bean);

        Class_Bean bean1=new Class_Bean();
        bean1.setAddress("5区402");
        bean1.setDay(1);
        bean1.setFrom(3);
        bean1.setTo(4);
        bean1.setName("计算方面的可视化");
        bean1.setTeacher("程登峰");
        bean1.setType("no");
        bean1.setWeekfrom(1);
        bean1.setWeekto(12);
        list.add(bean1);
        Class_Bean bean2=new Class_Bean();
        bean2.setAddress("5区402");
        bean2.setDay(1);
        bean2.setFrom(5);
        bean2.setTo(6);
        bean2.setName("计算方面的可视化");
        bean2.setTeacher("程登峰");
        bean2.setType("no");
        bean2.setWeekfrom(1);
        bean2.setWeekto(12);
        list.add(bean2);
        Class_Bean bean3=new Class_Bean();
        bean3.setAddress("5区402");
        bean3.setDay(1);
        bean3.setFrom(11);
        bean3.setTo(12);
        bean3.setName("计算方面的可视化");
        bean3.setTeacher("程登峰");
        bean3.setType("no");
        bean3.setWeekfrom(1);
        bean3.setWeekto(12);
        list.add(bean3);
        Log.d("个数大大大",list.size()+"$");
        new ScrollAdapter(list,view,getTextViews());
    }

    private void initViews(){
        list=new ArrayList<>();



    }

    //获取屏幕宽度
    private int getDisplayMetris(){
        return getResources().getDisplayMetrics().widthPixels;
    }

}
