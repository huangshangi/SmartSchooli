package fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Toast;


import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.Fragment_class_RecyclerView_adapter;
import adapter.Fragment_class_listView_adapter;
import adapter.ScrollAdapter;
import bean.Class_Bean;
import listener.Fragment_class_Change_Listener;
import listener.Fragment_class_Listener;
import utils.MyApplication;
import utils.NetworkLoader;
import utils.Util;

import static android.view.View.GONE;

//课堂界面
//scrollView与listView滑动冲突暂未处理
public class Fragment_class extends Fragment {

    View popupView;//弹出框控件

    View view;

    int allWeek=25;//学期总周数

    int currentWeek=1;//当前课程周数

    int  week=1;//当前所显示的课程周数

    List<Class_Bean> list;

    List<TextView>list_textView;

    LinearLayout linearlayout;//隐藏布局

    Button button_changeWeek;//修改当前周数的按钮

    RecyclerView recyclerView;

    Button button_current;//显示当前周数

    Fragment_class_RecyclerView_adapter adapter;

    ListView listView;

    Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_class_layout,container,false);

        initViews();

        initListeners();
        initEvents();
        refreshData(week);

        return view;
    }

    private void initEvents(){
        listView.setAdapter(new Fragment_class_listView_adapter(getActivity()));
        listView.setEnabled(false);

        list=NetworkLoader.getInstance().getList();
        NetworkLoader.getInstance().setFragment_class_listener(new Fragment_class_Listener() {
            @Override
            public void getClassDown(List<Class_Bean> list) {

                    Fragment_class.this.list=list;
                    //   获取数据并更新隐藏布局
                    Log.d("数据初始化",list.size()+"");
                    // 获取数据暂未实现，应当得到一个list<Class_Bean>
                    adapter = new Fragment_class_RecyclerView_adapter(list, week, allWeek);
                    adapter.setListener(new Fragment_class_Change_Listener() {
                        @Override
                        public void changeClass(int week) {
                            Fragment_class.this.week=week;
                            refreshData(week);
                        }
                     });
                    handler.sendEmptyMessage(0);


                }




        });


    }

    //监听器
    private void initListeners(){

        //点击显示或收起隐藏布局
        button_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // 检查当前隐藏布局显示状态
                if(linearlayout.getVisibility()==View.VISIBLE){
                    linearlayout.setVisibility(GONE);
                    //将当前周数改为当前周并显示当前周课表
                    week=currentWeek;

                    //显示某一周课表


                }else{
                     NetworkLoader.getInstance().getList();
                    linearlayout.setVisibility(View.VISIBLE);
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
        }
        return list_textView;
    }




    private void  refreshData(int week){
        //模拟从数据库获取数据
        list= Util.getInstance().getRealList(NetworkLoader.getInstance().getList(),week);
        new ScrollAdapter(list,view,getTextViews());
    }

    private void initViews(){

        popupView=LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.fragment_class_popup_addclass,null);

        list=new ArrayList<>();

        listView=(ListView)view.findViewById(R.id.fragment_class_listView);

        button_changeWeek=(Button)view.findViewById(R.id.fragment_class_inVisbile_button);

        recyclerView=(RecyclerView)view.findViewById(R.id.fragment_class_inVisbile_recyclerView);

        linearlayout=(LinearLayout)view.findViewById(R.id.fragment_class_inVisbile_layout);

        button_current=(Button)view.findViewById(R.id.fragment_class_currentweek);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);

            }
        };

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
