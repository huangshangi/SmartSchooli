package com.smartschool.smartschooli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import utils.NetworkLoader;

public class RegisterActivity extends AppCompatActivity {

    EditText text_id;

    EditText text_nickname;

    EditText text_pass1;

    EditText text_pass2;

    Button button_register;

    Spinner spinner;

    String type;//人员类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.register_layout);
        initViews();
        initListeners();
    }


    private void initViews(){

        text_id=(EditText)findViewById(R.id.register_id);

        text_nickname=(EditText)findViewById(R.id.register_nickname);

        text_pass1=(EditText)findViewById(R.id.register_pass1);

        text_pass2=(EditText)findViewById(R.id.register_pass2);

        button_register=(Button)findViewById(R.id.register_button);

        spinner=(Spinner)findViewById(R.id.register_spinner);
    }

    //为控件添加监听事件
    private void initListeners(){
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag_id=text_id.getText().toString()!=null&&!text_id.getText().toString().equals("");
                boolean flag_pass1=text_pass1.getText().toString()!=null&&!text_pass1.getText().toString().equals("");
                boolean flag_pass2=text_pass2.getText().toString()!=null&&!text_pass2.getText().toString().equals("");
                boolean flag_nick=text_nickname.getText().toString()!=null&&!text_nickname.getText().toString().equals("");
                if(flag_id&&flag_nick&&flag_pass1&&flag_pass2){
                    if(text_pass1.getText().toString().equals(text_pass2.getText().toString())){
                        NetworkLoader.getInstance().register(text_id.getText().toString(),text_pass1.getText().toString(),text_nickname.getText().toString(),type,RegisterActivity.this);
                    }else{
                        Toast.makeText(RegisterActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,"有字段为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    type="学生";
                }else if(position==1){
                    type="教师";
                }else if(position==2){
                    type="主管";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                    type="学生";
            }
        });




    }
}


