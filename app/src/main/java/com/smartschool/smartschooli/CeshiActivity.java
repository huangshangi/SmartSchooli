package com.smartschool.smartschooli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import listener.Wheel_listener;
import view.WheelView;

public class CeshiActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceshi);
        final List<String> list=new ArrayList<>();
        for(int i=0;i<30;i++)
            list.add("第"+i+"周");
        WheelView wheelView=(WheelView)findViewById(R.id.WheelView);
        wheelView.setPAGE_COUNT(5);
        wheelView.setList(list);

        wheelView.setWheel_listener(new Wheel_listener() {
            @Override
            public void onSelected(int position) {
                Toast.makeText(CeshiActivity.this,""+list.get(position),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
