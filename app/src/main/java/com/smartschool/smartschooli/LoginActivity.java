package com.smartschool.smartschooli;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import utils.NetworkLoader;
import utils.Util;

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
        //去掉标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_layout);

        initViews();//初始化控件

        initEvents();//进行权限的申请

        initListeners();//初始化点击事件
    }


    private void initEvents(){

        List<String>list_permission=new ArrayList<>();
        list_permission.add(Manifest.permission.READ_PHONE_STATE);
        list_permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        list_permission.add(Manifest.permission.CAMERA);
        Util.getInstance().requestPremissions(this,list_permission,1);
        Util.getInstance().requestPremission(this, Manifest.permission.READ_PHONE_STATE,2);

        Util.getInstance().requestPremission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,3);
        Util.getInstance().requestPremission(this,Manifest.permission.CAMERA,4);
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

                getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

    //进行单个权限的申请
    public static void requestPremission(Activity activity, String premission){
        if(ContextCompat.checkSelfPermission(activity,premission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{premission},1);
        }
    }
}
