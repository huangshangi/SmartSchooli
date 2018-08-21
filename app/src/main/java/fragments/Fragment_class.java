package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.Fragment_class_listView_adapter;
import adapter.ScrollAdapter;
import bean.Class_Bean;
import utils.MyApplication;

import static android.view.View.GONE;

//课堂界面
//scrollView与listView滑动冲突暂未处理
public class Fragment_class extends Fragment {

    View popupView;//弹出框控件

    View view;

    String allWeek;//学期总周数

    String currentWeek;//当前课程周数

    String week;//当前所显示的课程周数

    List<Class_Bean> list;

    List<TextView>list_textView;

    LinearLayout linearlayout;//隐藏布局

    Button button_changeWeek;//修改当前周数的按钮

    RecyclerView recyclerView;

    ImageView imageView;//点击显示隐藏布局

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_class_layout,container,false);

        initViews();
        ListView listView=(ListView)view.findViewById(R.id.fragment_class_listView);
        listView.setAdapter(new Fragment_class_listView_adapter(getActivity()));
        listView.setEnabled(false);
        initListeners();
        refreshData();

        return view;
    }

    //监听器
    private void initListeners(){

        //点击显示或收起隐藏布局
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //检查当前隐藏布局显示状态
                if(linearlayout.getVisibility()==View.VISIBLE){
                    linearlayout.setVisibility(GONE);
                    //将当前周数改为当前周

                }else if(linearlayout.getVisibility()==GONE){
                    linearlayout.setVisibility(View.VISIBLE);

                    //获取数据并更新隐藏布局

                    //获取数据暂未实现，应当得到一个map<String,list<Class_Bean>>

                }

            }
        });



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
            addTextViewListener(textView);
        }
        return list_textView;
    }


    //为7*12个textView设置监听事件
    private void addTextViewListener(final TextView textView){

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //使屏幕变暗
                lightOff();
                //弹出添加课程按钮
                PopupWindow popupWindow=new PopupWindow(popupView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.showAsDropDown(textView);
                int[]array=new int[2];
                textView.getLocationInWindow(array);
                Log.d("textView的位置:","宽度"+array[0]+"高度"+array[1]);
                popupWindow.setAnimationStyle(R.style.PopupAnimation);
                return false;
            }
        });
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

        popupView=LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.fragment_class_popup_addclass,null);

        list=new ArrayList<>();

        button_changeWeek=(Button)view.findViewById(R.id.fragment_class_inVisbile_button);

        recyclerView=(RecyclerView)view.findViewById(R.id.fragment_class_inVisbile_recyclerView);

        linearlayout=(LinearLayout)view.findViewById(R.id.fragment_class_inVisbile_layout);

        imageView=(ImageView)view.findViewById(R.id.fragment_class_change_image);

    }

    //获取屏幕宽度
    private int getDisplayMetris(){
        return getResources().getDisplayMetrics().widthPixels;
    }

    //使屏幕变亮
    private void lightOn(){
        WindowManager.LayoutParams params=getActivity().getWindow().getAttributes();
        params.alpha=1.0f;
        getActivity().getWindow().setAttributes(params);
    }


    //使屏幕变暗
    private void lightOff(){
        WindowManager.LayoutParams params=getActivity().getWindow().getAttributes();
        params.alpha=0.3f;
        getActivity().getWindow().setAttributes(params);

    }

}
