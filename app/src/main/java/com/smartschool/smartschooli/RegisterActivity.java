package com.smartschool.smartschooli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import utils.NetworkLoader;

public class RegisterActivity extends AppCompatActivity {

    EditText text_id;

    EditText text_nickname;

    EditText text_pass1;

    EditText text_pass2;

    Button button_register;

    CheckBox checkBox_stud;

    CheckBox checkBox_repair;

    String type;//人员类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initViews();
        initListeners();
    }


    private void initViews(){
        text_id=(EditText)findViewById(R.id.register_id);

        text_nickname=(EditText)findViewById(R.id.register_nickname);

        text_pass1=(EditText)findViewById(R.id.register_pass1);

        text_pass2=(EditText)findViewById(R.id.register_pass2);

        button_register=(Button)findViewById(R.id.button_register);

        checkBox_repair=(CheckBox)findViewById(R.id.kind_repairer);

        checkBox_stud=(CheckBox)findViewById(R.id.kind_stud);
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


        checkBox_repair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox_repair.isChecked()){
                    type=checkBox_repair.getText().toString();
                }
            }
        });

        checkBox_stud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox_stud.isChecked()){
                    type=checkBox_stud.getText().toString();
                }
            }
        });
    }
}


