package adapter;


import android.annotation.TargetApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

import java.util.List;
import java.util.Random;

import bean.Class_Bean;
import utils.MyApplication;

//用来摆放课程
public class ScrollAdapter {

    int colors[]=new int[]{R.color.red,R.color.green,R.color.blue,R.color.black};

    View view;//包含课程信息的ScrollView

    FrameLayout frameLayout;

    List<Class_Bean> list;//本周所有课程

    public ScrollAdapter(List<Class_Bean>list,View view){
        this.list=list;
        this.view=view;
        init();
    }

    private void init(){
        frameLayout=view.findViewById(R.id.fragment_class_framelayout);
        paintClasses(list);
    }

    private void  paintClasses(List<Class_Bean> list){
        for(Class_Bean bean:list){
            paintClass(bean);
        }
    }

    //摆放课程
    @TargetApi(21)
    private void paintClass(Class_Bean bean){
        TextView textView=new TextView(MyApplication.getContext());

        int danWei=getDanWidth();//TextView单位宽度
        int height=(bean.getTo()-bean.getFrom()+1)*danWei;//TextView实际高度
        textView.setHeight(height);
        textView.setWidth(danWei);
        textView.setGravity(Gravity.CENTER);

        //偏移计算
        int leftMargin = (bean.getDay() - 1) * getDanWidth();

        int topMargin=(bean.getFrom()-1)*getDanWidth();

        //设置偏移
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(danWei,height);
        params.setMargins(leftMargin,topMargin,0,0);
        textView.setLayoutParams(params);

        textView.setElevation(30.0f);

        //设置文字
        textView.setText(bean.getName()+"@"+bean.getAddress());
        //textView.setBackgroundColor(colors[(int)(new Random().nextInt(4))]);

        textView.setBackgroundResource(R.drawable.fragment_class_back1);

       // textView.setShadowLayer(5,10,10,R.color.red);
        //
        frameLayout.addView(textView);
        Log.d("######","课程已添加");
    }


    //获取屏幕宽度
    private int getDisMetrice(){
        return MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    //一个TExtView的单位宽高
    private int getDanWidth(){
        return getDisMetrice()/8;
    }
}
