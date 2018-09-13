package com.smartschool.smartschooli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import bean.Repair_Bean;
import listener.GetEvluateListener;
import utils.NetworkLoader;

public class ReadEvluateActivity extends AppCompatActivity {

    String id;

    Toolbar toolbar;

    RatingBar ratingBar1;

    RatingBar ratingBar2;

    RatingBar ratingBar3;

    TextView textView1;

    TextView textView2;

    TextView textView3;

    TextView textView;

    String array[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        id=getIntent().getStringExtra("id");

        array=new String[]{"非常差","差","一般","好","非常好"};
    }

    private void initEvents(){
        NetworkLoader.getInstance().getEvluate(id);
        NetworkLoader.getInstance().setGetEvluateListener(new GetEvluateListener() {
            @Override
            public void getDown(Repair_Bean bean) {
                String result=bean.getEvluate_content();
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
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
