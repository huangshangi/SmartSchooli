package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import utils.ImageLoader;
import utils.NetworkLoader;

//用来显示个人信息的activity
public class PersonActivity extends AppCompatActivity {


    RelativeLayout relativelayout1;

    RelativeLayout relativelayout2;

    RelativeLayout relativelayout3;

    RelativeLayout relativelayout4;

    ImageView imageView;

    TextView textView_nick;

    TextView textView_id;

    TextView textView_kind;

    Toolbar toolbar;

    List<String> list;//储存个人信息的list
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
        setContentView(R.layout.person_layout);
        initViews();

        initEvents();
    }


    private void initViews(){
        relativelayout1=(RelativeLayout)findViewById(R.id.person_layout_1);

        relativelayout2=(RelativeLayout)findViewById(R.id.person_layout_2);

        relativelayout3=(RelativeLayout)findViewById(R.id.person_layout_3);

        relativelayout4=(RelativeLayout)findViewById(R.id.person_layout_4);

        imageView=(ImageView)findViewById(R.id.person_layout_image);

        textView_id=(TextView)findViewById(R.id.person_layout_id);

        textView_nick=(TextView)findViewById(R.id.person_layout_nick);

        textView_kind=(TextView)findViewById(R.id.person_layout_kind);

        toolbar=(Toolbar)findViewById(R.id.person_layout_toolbar);

        list=new ArrayList();
    }

    private void initEvents(){

        list=NetworkLoader.getInstance().getPersonMessage();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView_id.setText(list.get(0));

        textView_nick.setText(list.get(2));

        textView_kind.setText(list.get(3));

        ImageLoader.getInstance().loadImage(list.get(1),imageView);
    }


}
