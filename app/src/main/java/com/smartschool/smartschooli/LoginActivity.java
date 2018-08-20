package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import utils.NetworkLoader;

//登录界面
public class LoginActivity extends AppCompatActivity {

    //用户id
    String id;
    //用户密码
    String password;

    EditText text_id;

    EditText text_pass;

    Button button_login;

    Button button_register;

    boolean flag_id=true;//判断id框是否为空

    boolean flag_pass=true;//判断密码框是否为空

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);

        initViews();//初始化控件

        initListeners();//初始化点击事件
    }


    private void initViews(){

        text_id=(EditText)findViewById(R.id.login_id);

        text_pass=(EditText)findViewById(R.id.login_pass);

        button_login=(Button)findViewById(R.id.button_login);

        button_register=(Button)findViewById(R.id.button_register);
    }

    private void initListeners(){

        //监控id框输入变化
        text_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                id=text_id.getText().toString();
                if(id!=null&&!id.equals(""))
                    flag_id=false;

                //设置登录按钮是否可点击
                button_login.setEnabled(isEnable(flag_id,flag_pass));

            }
        });

        //监控pass框输入变化
        text_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password=text_pass.getText().toString();
                if(password!=null&&!password.equals(""))
                    flag_pass=false;
                //设置登录按钮是否可点击
                button_login.setEnabled(isEnable(flag_id,flag_pass));
            }
        });

        //登录按钮
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"我被点击",Toast.LENGTH_SHORT).show();
                //询问是否可登陆
                NetworkLoader.getInstance().login(id,password,LoginActivity.this);

            }
        });

        //注册按钮
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    //判断登录按钮是否可点击
    private boolean isEnable(boolean flag_id,boolean flag_pass){
        if(!flag_id&&!flag_pass)
            return true;
        return false;
    }
}
