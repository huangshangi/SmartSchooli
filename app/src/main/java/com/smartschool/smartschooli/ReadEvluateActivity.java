package com.smartschool.smartschooli;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import bean.Repair_Bean;
import listener.GetEvluateListener;
import utils.NetworkLoader;

public class ReadEvluateActivity extends AppCompatActivity {

    Repair_Bean bean;

    Toolbar toolbar;

    RatingBar ratingBar1;

    RatingBar ratingBar2;

    RatingBar ratingBar3;

    TextView textView1;

    TextView textView2;

    TextView textView3;

    TextView textView;

    String array[];

    Handler handler;

    boolean flag;
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
        setContentView(R.layout.evluate_read_layout);
        initViews();
        initEvents();
    }

    private void initViews(){
        toolbar=(Toolbar)findViewById(R.id.evluate_layout_toolbar);
        ratingBar1=(RatingBar)findViewById(R.id.ratingResult);
        textView1=(TextView)findViewById(R.id.textView1);
        ratingBar2=(RatingBar)findViewById(R.id.ratingSpeed);
        textView2=(TextView)findViewById(R.id.textView2);
        ratingBar3=(RatingBar)findViewById(R.id.ratingEvaluate);
        textView3=(TextView)findViewById(R.id.textView3);
        textView=(TextView)findViewById(R.id.textView);
        flag=getIntent().getBooleanExtra("flag",false);
        bean=(Repair_Bean) getIntent().getSerializableExtra("bean");

        array=new String[]{"非常差","差","一般","好","非常好"};
    }

    private void initEvents(){

        if(flag){
            toolbar.setTitle("我的评价");
        }else{
            toolbar.setTitle("对我的评价");
        }
        String result=bean.getEvluate_content();
        Log.d("resultresult",result);
        int index=result.lastIndexOf("￥");
        String[]fenshus=result.substring(0,index).split("￥");
        int fenshu1=Integer.valueOf(fenshus[0]);
        int fenshu2=Integer.valueOf(fenshus[1]);
        int fenshu3=Integer.valueOf(fenshus[2]);
        ratingBar1.setRating(fenshu1);
        textView1.setText(array[fenshu1-1]);
        ratingBar2.setRating(fenshu2);
        textView2.setText(array[fenshu2-1]);
        ratingBar3.setRating(fenshu3);
        textView3.setText(array[fenshu3-1]);
        textView.setText(result.substring(index+1));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
