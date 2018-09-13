package com.smartschool.smartschooli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import adapter.Publish_gridview_adapter;
import bean.Repair_Bean;
import view.Submit_repair_GridView;


//需要接受一个Repair_Bean对象
public class RepairDetailActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    TextView textView_machine;

    TextView textView_type;

    TextView textView_address;

    TextView textView_content;

    TextView textView_handle;

    TextView textView_evluate;

    Submit_repair_GridView gridView;

    Repair_Bean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair_detail_layout);
        bean=(Repair_Bean) getIntent().getSerializableExtra("repairMessage");
        initViews();
        initEvents();
    }


    private void initViews(){
        linearLayout=(LinearLayout)findViewById(R.id.linearlayout);
        textView_address=(TextView)findViewById(R.id.repair_details_address);
        textView_machine=(TextView)findViewById(R.id.repair_detail_machine);
        textView_content=(TextView)findViewById(R.id.repair_detail_content);
        textView_type=(TextView)findViewById(R.id.repair_detail_type);
        textView_evluate=(TextView)findViewById(R.id.repair_details_evaluate);
        textView_handle=(TextView)findViewById(R.id.repair_details_handle);
        gridView=(Submit_repair_GridView)findViewById(R.id.gridview);
    }

    private void initEvents(){
       linearLayout.setVisibility(View.GONE);
       textView_type.setText(bean.getRepair_type());
       textView_content.setText(bean.getRepair_content());
       textView_machine.setText(bean.getRepair_machine());
       textView_address.setText(bean.getRepair_adress());
       if(bean.getEvluate_status().equals("否")) {
           textView_evluate.setText("暂无评价");
       }else{
           textView_evluate.setText(bean.getEvluate_content());
       }
       if(bean.getHandle()){
           textView_handle.setText("已处理");
       }else{
           textView_handle.setText("未处理");
       }

       //将string转化为list
        List<String> list= Arrays.asList(bean.getRepair_urls().split("￥"));
       gridView.setAdapter(new Publish_gridview_adapter(this,list));

    }
}
