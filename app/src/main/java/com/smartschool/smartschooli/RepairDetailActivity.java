package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import adapter.Publish_gridview_adapter;
import bean.Repair_Bean;
import utils.NetworkLoader;
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

    Toolbar toolbar;

    Repair_Bean bean;

    String type;

    private LinearLayout linearLayout_visible;

    private Button button_handle;

    private Button button_concat;

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
        setContentView(R.layout.repair_detail_layout);
        bean=(Repair_Bean) getIntent().getSerializableExtra("repairMessage");
        type=getIntent().getStringExtra("type");
        initViews();
        initEvents();
        initListeners();
    }


    private void initListeners(){
        button_concat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RepairDetailActivity.this,ChatActivity.class);
                intent.putExtra("target_id",bean.getRepairer_id());
                intent.putExtra("target_name",bean.getRepairer_name());
                intent.putExtra("own_id", NetworkLoader.getInstance().getPersonMessage().get(0));
                startActivity(intent);
            }
        });


        button_handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_handle.getText().toString().equals("未解决")){
                    button_handle.setText("已解决");
                    button_handle.setEnabled(false);
                    button_handle.setBackgroundResource(R.color.gray);
                    NetworkLoader.getInstance().updateHandle(bean.getRepair_bianhao());
                }
            }
        });
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
        linearLayout_visible=(LinearLayout)findViewById(R.id.visible);
        button_handle=(Button)findViewById(R.id.button_handle);
        button_concat=(Button)findViewById(R.id.button_concat);
        toolbar=(Toolbar)findViewById(R.id.repair_details_layout_toolbar);
    }


    private void initEvents(){
        if(type.equals("主管")){
            linearLayout_visible.setVisibility(View.VISIBLE);
        }else{
            linearLayout_visible.setVisibility(View.INVISIBLE);
        }
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

       if(bean.getHandle()) {
           button_handle.setText("已解决");
           button_handle.setEnabled(false);
           button_handle.setBackgroundResource(R.color.gray);
       }else{
           button_handle.setEnabled(true);
           button_handle.setText("未解决");
           button_handle.setBackgroundResource(R.color.royalblue);
       }


       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
    }



}
