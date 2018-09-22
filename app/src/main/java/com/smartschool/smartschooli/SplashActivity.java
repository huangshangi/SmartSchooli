package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import cn.bmob.v3.BmobUser;
import listener.AnimMeasureListener;
import listener.AnimationListener;
import utils.NetworkLoader;
import view.LauncherView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("执行闪屏页内容", "");
        //隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_layout);
        final LauncherView launcherView=(LauncherView)findViewById(R.id.launcherView);
        launcherView.postDelayed(new Runnable() {
            @Override
            public void run() {
                launcherView.start();
            }
        },500);



        launcherView.setAnimationListener(new AnimationListener() {
            @Override
            public void loadingDown() {
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }
                prepareThing();
            }
        });
    }


    private void initImage() {
        ImageView iv_start = (ImageView) findViewById(R.id.start);
        iv_start.setImageResource(R.drawable.splash);
        //进行缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(4000);
        //动画播放完成后保持形状
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //可以在这里先进行某些操作

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                prepareThing();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_start.startAnimation(scaleAnimation);
    }



    private void prepareThing() {
        //检查用户是否登录

        BmobUser bmobUser = BmobUser.getCurrentUser();

        if (bmobUser == null || bmobUser.getUsername().equals("")) {
            //未登录
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //进入主界面
            if (bmobUser.getEmailVerified()) {
                Intent intent = new Intent(this, Z_MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                NetworkLoader.getInstance().getList();
                try {
                    NetworkLoader.getInstance().getSemaphore_getList().acquire();
                } catch (Exception e) {
                    Log.d("error", e.getMessage());
                }

                Intent intent = new Intent(this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        }
    }
}
