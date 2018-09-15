package com.smartschool.smartschooli;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import bean.Class_Bean;
import bean.Qiandao_Bean;
import listener.Fragment_class_getQiandao_listener;
import utils.NetworkLoader;
import utils.Util;

public class FcTeacherActivity extends AppCompatActivity {

    int height=150;//生成二维码的高 dp

    int width=150;//生成二维码的宽  dp

    Toolbar toolbar;

    ArrayList<Class_Bean> list;//储存本周课程

    ImageView imageView;//显示二维码的图片

    TextView textView;//显示课程信息

    SwipeRefreshLayout swipeRefreshLayout;//下拉刷新

    LinearLayout linearLayout;//可隐藏的linearLayout

    boolean flag=false;//判断adapter是否已初始化

    ListView listView;

    Adapter adapter;//listView适配器

    Class_Bean bean;//储存当前课程信息

    int currentClass;//当前课程节数

    int currentDay;//当前是周几

    int currentWeek;//当前处于第几周

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //如果系统5.0以上
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.royalblue));
        }
        setContentView(R.layout.fragment_class_teacher_layout);
        //检查当前是否有权限生成二维码
        list=(ArrayList<Class_Bean>)getIntent().getSerializableExtra("list");
        bean=checkFlag();
        initViews();
        initEvents();
        initListeners();
        refreshData();
    }


    private void initViews(){
        imageView=(ImageView)findViewById(R.id.fragment_class_teacher_layout_imageView);

        textView=(TextView)findViewById(R.id.fragment_class_teacher_layout_textView);

        listView=(ListView)findViewById(R.id.fragment_class_teacher_layout_listView);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.fragment_class_teacher_swipeLayout);

        linearLayout=(LinearLayout)findViewById(R.id.linearlayout);

        toolbar=findViewById(R.id.fragment_class_teacher_layout_toolbar);
        toolbar.setTitle("");
    }

    private void refreshData(){
        bean=checkFlag();
        updateEvent();
        if(bean!=null) {
            NetworkLoader.getInstance().getQiandao(bean.getcNO(), bean.getCourseNumber(), "" + currentWeek, "" + currentDay, "" + currentClass);
        }
    }


    private void initEvents(){
        updateEvent();

        NetworkLoader.getInstance().setFragment_class_getQiandao_listener(new Fragment_class_getQiandao_listener() {
            @Override
            public void getDown(List<Qiandao_Bean> list) {
                bean=checkFlag();
                if(bean!=null) {
                    updateAdapter(list);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initListeners(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bean=checkFlag();
                if(bean==null){
                    swipeRefreshLayout.setRefreshing(false);
                }else {
                    refreshData();
                }
            }
        });
    }

    private void updateEvent(){
        if(bean==null){
            textView.setText("当前没有正在进行的课程");
//            imageView.setImageBitmap(createCeshiQR());
            linearLayout.setVisibility(View.GONE);
        }else{
            Bitmap bitmap=createQR();
            textView.setText("当前正在进行的课程:"+bean.getName()+"   "+bean.getAddress());
            imageView.setImageBitmap(bitmap);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap createCeshiQR(){
        String message="sd03020250!3!12!2!3";
        return EncodingUtils.createQRCode(message,Util.getInstance().dp2px(width), Util.getInstance().dp2px(height),null);
    }

    private Bitmap createQR(){
        String message=bean.getcNO()+"!"+bean.getCourseNumber()+"!"+Util.getInstance().getCurrentWeek()+"!"+bean.getDay()+"!"+currentClass;
        Bitmap bitmap=EncodingUtils.createQRCode(message,Util.getInstance().dp2px(width), Util.getInstance().dp2px(height),null);
        return bitmap;
    }

    private Class_Bean checkFlag(){


        currentClass= Util.getInstance().getClassNumber();

        currentDay=Util.getInstance().getCurrentDay();

        currentWeek=Util.getInstance().getCurrentWeek();

        for(int i=0;i<list.size();i++){
            Class_Bean bean=list.get(i);
            if(currentWeek<=bean.getWeekto()&&currentWeek>=bean.getWeekfrom()){
                if(currentDay==bean.getDay()){
                    if(currentClass<=bean.getTo()&&currentClass>=bean.getFrom()){
                        return bean;
                    }
                }
            }
        }


        return null;
    }


    private void updateAdapter(List<Qiandao_Bean>list){
        if(flag){
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }else{
            adapter=new Adapter(list);
            listView.setAdapter(adapter);
        }
    }

    class Adapter extends BaseAdapter{

        List<Qiandao_Bean>list;

        public Adapter(List<Qiandao_Bean>list){
            this.list=list;
        }

        public void setList(List<Qiandao_Bean>list){
            this.list=list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder=new ViewHolder();
                viewHolder.textView1=(TextView)convertView.findViewById(R.id.fragment_class_listView_text1);
                viewHolder.textView2=(TextView)convertView.findViewById(R.id.fragment_class_listView_text2);
                viewHolder.textView3=(TextView)convertView.findViewById(R.id.fragment_class_listView_text3);
                viewHolder.textView4=(TextView)convertView.findViewById(R.id.fragment_class_listView_text4);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }

            Qiandao_Bean bean=list.get(position);
            viewHolder.textView1.setText(position+1+"");
            viewHolder.textView2.setText(bean.getXuehao());
            viewHolder.textView3.setText(bean.getName());
            viewHolder.textView4.setText(bean.getCreatedAt());

            return convertView;
        }

        class ViewHolder{
            TextView textView1;//序号

            TextView textView2;//学号

            TextView textView3;//姓名

            TextView textView4;//时间
        }
    }
}
