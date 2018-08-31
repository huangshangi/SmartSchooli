package com.smartschool.smartschooli;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.bmob.v3.BmobUser;
import utils.NetworkLoader;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("执行闪屏页内容","");

        //检查用户是否登录
        BmobUser bmobUser=BmobUser.getCurrentUser();

        if(bmobUser==null||bmobUser.getUsername().equals("")){
            //未登录
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }else{
            //进入主界面
            Log.d("jinrule","名字"+bmobUser+"喵喵"+BmobUser.getCurrentUser().getUsername());
            NetworkLoader.getInstance().getList();
            try {
                NetworkLoader.getInstance().getSemaphore_getList().acquire();
            }catch (Exception e){
                Log.d("error",e.getMessage());
            }

            Intent intent=new Intent(this,MainActivity.class);

            startActivity(intent);
            finish();
        }

    }
}
