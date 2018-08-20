package utils;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import cn.bmob.v3.Bmob;

//应用第一次创建时会初始化该类,可用MyApplication.getContext()获得一个context变量，注意：该变量在创建对话框时不建议使用
//该类用于全局获取context变量
public class MyApplication extends Application {


    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyApplication.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"be722c6e7cc9bed287aefcebfce944a5");
        context=getApplicationContext();


    }



}
