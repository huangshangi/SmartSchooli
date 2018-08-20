package utils;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;


import com.smartschool.smartschooli.MainActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


import bean.Person_Bean;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.content.Context.TELEPHONY_SERVICE;

/*
    采用线程池
    建议与网络有关的操作都在该类中进行
    使用方法：
        NetWorkLoader.getInstance()获取一个NetworkLoader实例
        当需要执行一个任务时调用addTask(Runnable runable)将任务添加至队列，并在任务执行完毕后调用semaphore.release()方法


 */

public class NetworkLoader {



    private String phone_id;//手机唯一码

    final static int THREAD_COUNT=3;//线程数

    static NetworkLoader networkLoader;



    private ExecutorService mThreadPool;//线程池

    private LinkedList<Runnable>linkedList;//线程池任务队列

    private Handler mUiHandler;//UI更新handler

    private Handler mTaskHandler;//负责通知线程池有新任务加入

    private Thread thread;//后台轮询线程

    private Semaphore semaphore;//每执行完一个任务时会释放一个信号

    private Semaphore s;//该信号量标记mHandlerPool初始化完毕


    public static NetworkLoader getInstance(){
        if(networkLoader==null){
            synchronized (NetworkLoader.class){
                if(networkLoader==null){
                    networkLoader=new NetworkLoader();
                }
            }
        }
        return networkLoader;
    }

    //构造方法，进行控件的初始化
    private NetworkLoader(){


        mThreadPool= Executors.newFixedThreadPool(THREAD_COUNT);

        linkedList=new LinkedList<>();



        semaphore=new Semaphore(THREAD_COUNT);

        s=new Semaphore(0);



        phone_id=setPhone_id();

        thread=new Thread(){

            @Override
            public void run() {
                Looper.prepare();

                mTaskHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        try {
                            semaphore.acquire();
                        }catch (Exception e){
                            Log.d("error occur",e.getMessage());
                        }
                        mThreadPool.execute(getTask());
                    }
                };

                //mTaskHandler初始化完毕释放一个信号量标记
                s.release();
                //不断地从MessageQueue中取出信息并处理，如果有的话
                Looper.loop();

            }
        };

        thread.start();
    }




    //设置手机唯一识别码
    private String setPhone_id(){
        TelephonyManager TelephonyMgr = (TelephonyManager)MyApplication.getContext().getSystemService(TELEPHONY_SERVICE);
        if(ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
            phone_id = TelephonyMgr.getDeviceId();
            return phone_id;
        }else{

        }

        return "ceshiphone_id";

    }



    //向队列添加一个任务
    private synchronized  void addTask(Runnable runnable){
        linkedList.add(runnable);

        //保证在后台轮询线程初始化完毕后再通知
        if(mTaskHandler==null){
            try {
                s.acquire();
            }catch (Exception e){
                Log.d("error occur",e.getMessage());
            }
        }
        //向队列添加一个任务后，通知后台轮询线程执行该任务
        mTaskHandler.sendEmptyMessage(0x110);
    }


    //取出一个任务
    private Runnable getTask(){
        return linkedList.removeFirst();
    }


    //登录方法
    public void login(final String id,final String password,final Activity activity){

        addTask(new Runnable() {
            @Override
            public void run() {
                Person_Bean bean=new Person_Bean();
                bean.setUsername(id);
                bean.setPassword(password);

                bean.login(new SaveListener<Person_Bean>() {
                    @Override
                    public void done(Person_Bean person_bean, BmobException e) {
                        if(e==null){

                            //将信息储存至Sharepreference
                            SharedPreferences.Editor editor=MyApplication.getContext().getSharedPreferences("Person_Data", Context.MODE_PRIVATE).edit();
                            editor.putString("id",person_bean.getUsername());
                            editor.putString("image",person_bean.getImage());
                            editor.putString("nickname",person_bean.getNickname());
                            editor.putString("kind",person_bean.getKind());
                            editor.apply();

                            //登陆成功，进入主界面
                           Intent intent=new Intent(activity, MainActivity.class);
                           activity.startActivity(intent);
                            activity.finish();


                        }else{
                            Log.d("33333333333","登陆失败"+e.getMessage());
                        }
                    }
                });

                //释放资源
                semaphore.release();
            }
        });
    }


    //注册方法
    public void register(final String id, final String pass, final String nickname, final String type, final Activity activity){
        addTask(new Runnable() {
            @Override
            public void run() {
                Person_Bean bean=new Person_Bean();

                //默认头像
                bean.setImage("http://bmob-cdn-20795.b0.upaiyun.com/2018/08/19/b5fb9c5d401dbf2f80f47f206109ec57.jpg");
                bean.setNickname(nickname);
                bean.setUsername(id);
                bean.setPassword(pass);
                bean.setKind(type);
                bean.signUp(new SaveListener<Person_Bean>() {
                    @Override
                    public void done(Person_Bean o, BmobException e) {
                        if(e==null){
                            //注册成功
                            activity.finish();
                        }else{
                            Toast.makeText(activity,"注册失败,"+s,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                semaphore.release();
            }
        });
    }


    /*从本地获取个人信息,返回一个list
        list[0]->id
        list[1]->image
        list[2]->nickname
        list[3]->kind
    */
    public List<String> getPersonMessage(){
        List<String> list=new ArrayList();
        SharedPreferences sharedPreferences=MyApplication.getContext().getSharedPreferences("Person_Data",0);
        String id=sharedPreferences.getString("id","");
        String image=sharedPreferences.getString("image","");
        String nickname=sharedPreferences.getString("nickname","");
        String kind=sharedPreferences.getString("kind","");
        list.add(id);
        list.add(image);
        list.add(nickname);
        list.add(kind);
        return list;
    }

}
