package com.smartschool.smartschooli;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import bean.Class_Bean;
import utils.Util;

public class FcTeacherActivity extends AppCompatActivity {

    int height=150;//生成二维码的高 dp

    int width=150;//生成二维码的宽  dp

    ArrayList<Class_Bean> list;//储存本周课程

    ImageView imageView;//显示二维码的图片

    TextView textView;//显示课程信息

    ListView listView;//

    Class_Bean bean;//储存当前课程信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_class_teacher_layout);
        //检查当前是否有权限生成二维码
        bean=checkFlag();
        initViews();
        initEvents();
    }


    private void initViews(){
        imageView=(ImageView)findViewById(R.id.fragment_class_teacher_layout_imageView);

        textView=(TextView)findViewById(R.id.fragment_class_teacher_layout_textView);

        listView=(ListView)findViewById(R.id.fragment_class_teacher_layout_listView);
    }


    private void initEvents(){
        if(bean==null){
            textView.setText("当前没有正在进行的课程");
        }else{
            Bitmap bitmap=createQR();
            textView.setText("当前正在进行的课程:"+bean.getName()+"   "+bean.getAddress());
        }
    }

    private Bitmap createQR(){
        String message=bean.getcNO()+"!"+bean.getCourseNumber()+"!"+Util.getInstance().getCurrentWeek()+"!"+bean.getDay();
        Bitmap bitmap=EncodingUtils.createQRCode(message,Util.getInstance().dp2px(width), Util.getInstance().dp2px(height),null);
        return bitmap;
    }

    private Class_Bean checkFlag(){


        int currentClass= Util.getInstance().getClassNumber();

        int currentDay=Util.getInstance().getCurrentDay();

        int currentWeek=Util.getInstance().getCurrentWeek();

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
}
