package adapter;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

import java.util.List;
import java.util.Random;

import bean.Class_Bean;
import utils.MyApplication;
import utils.Util;

//用来摆放课程
public class ScrollAdapter {

    final int DISTANCE=1;

    int colors[]=new int[]{R.color.red,R.color.green,R.color.blue,R.color.black};

    View view;//包含课程信息的ScrollView

    FrameLayout frameLayout;

    List<Class_Bean> list;//本周所有课程



    ListView listView;

    public ScrollAdapter(List<Class_Bean>list,View view){
        this.list=list;
        this.view=view;

        init();
    }

    private void init(){
        frameLayout=view.findViewById(R.id.fragment_class_framelayout);

        listView=(ListView)view.findViewById(R.id.fragment_class_listView);
        paintClasses(list);
       // paintBack(list_text);
    }

    private void paintBack(List<TextView>list_text){

        int danWei=getDanWidth();
        for(int i=1;i<list_text.size()+1;i++){
            //计算该textView所在的行列
            int row=(i%7==0)?(i/7):(i/7+1);//行
            int coloum=(i%7==0?7:i%7);//列

            //计算边距
            int leftMargin=coloum*danWei;
            int topMargin=(row-1)*danWei+dp2px((row-1)*DISTANCE);

            //进行摆放
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(danWei,danWei);
            params.setMargins(leftMargin,topMargin,0,0);
            list_text.get(i-1).setLayoutParams(params);
            frameLayout.addView(list_text.get(i-1));
        }

    }

    private void  paintClasses(List<Class_Bean> list){
        frameLayout.removeAllViews();
        frameLayout.addView(listView);
        for(Class_Bean bean:list){
            paintClass(bean);
        }
    }

    //摆放课程

    private void paintClass(Class_Bean bean){
        TextView textView=new TextView(MyApplication.getContext());
//
        int danWei=getDanWidth();//TextView单位宽度
        int height=(bean.getTo()-bean.getFrom()+1)*danWei;//TextView实际高度



        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);

        //偏移计算
        int leftMargin = (bean.getDay()) * getDanWidth()+dp2px(3);

        int topMargin=(bean.getFrom()-1)*getDanWidth()+dp2px((bean.getFrom()-1)* DISTANCE)+dp2px(3);
        Log.d("课程",bean.getName()+bean.getFrom()+"("+bean.getDay());

        //设置偏移
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(danWei-dp2px(6),height-dp2px(6));
        params.setMargins(leftMargin,topMargin,0,0);

        textView.setLayoutParams(params);

        String name=bean.getName();

        String address=bean.getAddress();

        if(address==null){
            address="未知";
        }

        //去括号
        if(name.contains("(")){
            int index=bean.getName().indexOf("(");
            int lastIndex=bean.getName().lastIndexOf(")");
           name=bean.getName().substring(0,index)+bean.getName().substring(lastIndex+1);
        }
        //设置文字
        if(name.length()>4){
            textView.setText(name.substring(0, 3) + "..." + "\n@" + address);
        }else{
            textView.setText(name + "@" + address);
        }
        textView.setBackgroundResource(R.drawable.fragment_class_back1);


        //
        frameLayout.addView(textView);

        Log.d("######","课程已添加"+leftMargin+"#"+topMargin);
    }


    //获取屏幕宽度
    private int getDisMetrice(){
        return MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    //一个TExtView的单位宽高
    private int getDanWidth(){
        return getDisMetrice()/8;
    }

    private int px2dp(float pxValue) {
        final float scale =  MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    private int dp2px(float dpValue){
        float scale=MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
}
