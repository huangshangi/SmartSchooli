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
import android.webkit.HttpAuthHandler;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartschool.smartschooli.MainActivity;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


import bean.Class_Bean;
import bean.Person_Bean;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import listener.Fragment_class_Listener;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private ArrayList<Class_Bean>list_bean;//盛放一周课程信息


    private ExecutorService mThreadPool;//线程池

    private LinkedList<Runnable>linkedList;//线程池任务队列

    private Handler mUiHandler;//UI更新handler


    private Handler mTaskHandler;//负责通知线程池有新任务加入

    private Thread thread;//后台轮询线程

    private Semaphore semaphore_getList;//判断是否成功获取课程信息

    private Semaphore semaphore;//每执行完一个任务时会释放一个信号

    private Semaphore s;//该信号量标记mHandlerPool初始化完毕

    private static Map<Integer,List<Class_Bean>>hashMap;//储存课程信息 单例

    Fragment_class_Listener fragment_class_listener;


    public void setFragment_class_listener(Fragment_class_Listener fragment_class_listener){
        this.fragment_class_listener=fragment_class_listener;
    }

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

        semaphore_getList=new Semaphore(0);

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
                            editor.putString("pass",person_bean.getPass());

                            editor.apply();

                            NetworkLoader.getInstance().getList();
                            try {
                                NetworkLoader.getInstance().getSemaphore_getList().acquire();
                            }catch (Exception e1){
                                Log.d("error",e1.getMessage());
                            }

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
                bean.setPass(Util.getMD5Str(pass));

                //进行注册账号检查
                verfiyZhanghao(bean,pass,activity);


                semaphore.release();
            }
        });
    }

    //验证账号有效性
    private void verfiyZhanghao(final Person_Bean bean,final String pass,final Activity activity){
        final String id=bean.getUsername();

        addTask(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                RequestBody requestBody=new FormBody.Builder().add("j_username",id).add("j_password",Util.getMD5Str(pass)).build();
                Request request=new Request.Builder().post(requestBody).url("http://bkjws.sdu.edu.cn/b/ajaxLogin").build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.code()==200){
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
                    }else{
                        //账号无效
                        Toast.makeText(MyApplication.getContext(),"账号或密码无效",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }
               semaphore.release();
            }
        });
    }



    public Semaphore getSemaphore_getList(){
        return semaphore_getList;
    }
    /*从本地获取个人信息,返回一个list
        list[0]->id
        list[1]->image
        list[2]->nickname
        list[3]->kind
        list[4]->pass(已加密)
        list[5]->学生姓名
    */
    public List<String> getPersonMessage(){
        List<String> list=new ArrayList();
        SharedPreferences sharedPreferences=MyApplication.getContext().getSharedPreferences("Person_Data",0);
        String id=sharedPreferences.getString("id","");
        String image=sharedPreferences.getString("image","");
        String nickname=sharedPreferences.getString("nickname","");
        String kind=sharedPreferences.getString("kind","");
        String pass=sharedPreferences.getString("pass","");
        String name=sharedPreferences.getString("studentName","");
        list.add(id);
        list.add(image);
        list.add(nickname);
        list.add(kind);
        list.add(pass);
        list.add(name);
        return list;
    }


    public  Map<Integer,List<Class_Bean>> getHashMap(){
        if(hashMap==null){
            synchronized (NetworkLoader.class){
                if (hashMap==null){
                    getClassesMessage();
                }
            }
        }
        return hashMap;
    }

    public ArrayList<Class_Bean>getList(){
        if(list_bean==null){
            synchronized (NetworkLoader.class){
                if(list_bean==null){
                    getClassesMessage();
                }
            }
        }else{
            semaphore_getList.release();
            if(fragment_class_listener!=null){
                fragment_class_listener.getClassDown(list_bean);
            }
        }
        return list_bean;
    }

    public void Class_init(){
        if(list_bean==null){
            synchronized (NetworkLoader.class){
                if(list_bean==null){
                    getClassesMessage();
                }
            }
        }
    }

    //爬取课表信息
    public  void getClassesMessage(){

        addTask(new Runnable() {
            @Override
            public void run() {

                List<String>list=getPersonMessage();
                String login_url="http://bkjws.sdu.edu.cn/b/ajaxLogin";//教务系统登录界面

                String main_url="http://bkjws.sdu.edu.cn/f/xk/xs/bxqkb";//课程表界面

                String detail_url="http://bkjws.sdu.edu.cn/b/grxx/xs/xjxx/detail";//详细信息界面
                String username=list.get(0);
                String password=list.get(4);

                final Map<String,List<Cookie>> hashMap=new HashMap<>();

                OkHttpClient client=new OkHttpClient.Builder().cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        hashMap.put(httpUrl.host(),list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie>list=hashMap.get(httpUrl.host());

                        return list==null?new ArrayList<Cookie>():list;
                    }
                }).build();
                try {
                    RequestBody requestBody = new FormBody.Builder().add("j_username", username).add("j_password",password).build();

                    Request request = new Request.Builder().url(login_url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    Log.d("jinrule",response.body().string()+password+"@"+username);


                    if (response.code() == 200) {

                        //获取姓名
                        Request request2=new Request.Builder().url(detail_url).build();
                        Response response2=client.newCall(request2).execute();
                        String result=response2.body().string();
                        JSONObject jsonObject=new JSONObject(result);
                        JSONObject jsonObject2=(JSONObject) jsonObject.get("object");

                        String name=(String) jsonObject2.get("xm");
                        SharedPreferences.Editor editor=MyApplication.getContext().getSharedPreferences("Person_Data", Context.MODE_PRIVATE).edit();
                        editor.putString("studentName",name);
                        editor.apply();

                        Request request1=new Request.Builder().url(main_url).build();
                        Response response1=client.newCall(request1).execute();
                        String html=response1.body().string();
                        Log.d("jinrule","ddddd"+name);
                        //使用jsoup解析获得的数据
                        parseHtml(html);
                    }
                }catch (Exception e){
                    Log.d("error","爬取网页时出现异常"+e);
                }

                semaphore_getList.release();
                semaphore.release();
            }
        });

    }


    //返回一个hashmap<String,List<Class_Bean>> String代表周几，list内部储存当天课程信息
    private  void parseHtml(String html){

        list_bean=new ArrayList<>();//储存自己组装的Class_Bean

        List<String>list=new ArrayList<>();//储存原生课表，示例如下1$sd01511480$孙子兵法与三十六计(国学)$601$2$任选$材料学院$耿贵立$1-16周上$5$5$中心理综楼609d$

        Document document= Jsoup.parse(html);

        Elements div=document.select("#ysjddDataTableId");

        Elements trs=div.select("tr");

        Log.d("shujudangqian","html个数"+trs.size());

        for(int i=1;i<trs.size();i++) {
            String data="";//每一条数据

            Elements tds = trs.get(i).select("td");
            for (Element element : tds) {
                data += element.getElementsByTag("td").text() + "!";
            }
            Log.d("shujudangqian",data);
            list.add(data);

        }

        //将原生list转化为需要的list
        for(int i=0;i<list.size();i++) {
            Class_Bean bean=getClass_bean(list.get(i));
            list_bean.add(bean);
        }

        //将list<Class_Bean>组装成map<Integer,List<Class_bean>
        Map<Integer,List<Class_Bean>> map=getClass_Map(list_bean);



    }
    //将list<Class_Bean>组装成map<Integer,List<Class_bean>
    private  Map<Integer, List<Class_Bean>> getClass_Map(ArrayList<Class_Bean>list){
        hashMap=new HashMap<Integer,List<Class_Bean>>();
        for(Class_Bean bean:list){
            if(hashMap.keySet().contains(bean.getDay())){
                hashMap.get(bean.getDay()).add(bean);
            }else{
                List<Class_Bean>list1=new ArrayList<>();
                list1.add(bean);
                hashMap.put(bean.getDay(),list1);
            }

        }
        Log.d("fangfa","准备回调");
        if(fragment_class_listener!=null){
            fragment_class_listener.getClassDown(list);
            Log.d("fangfa","回调成功");

        }
        return hashMap;
    }


    //将‘3$sd03020250$概率与统计$0$3$任选$软件学院$王薇4$1-12周上$2$3$软件园5区208d$’组装成Class_bean对象
    private Class_Bean getClass_bean(String string){
        List<String> list_result = Arrays.asList(string.split("!"));

        Class_Bean bean=new Class_Bean();
        bean.setcNO(list_result.get(1));
        bean.setCourseNumber(list_result.get(3));
        bean.setName(list_result.get(2));
        bean.setTeacher(list_result.get(7));
        if(list_result.size()>11)
            bean.setAddress(list_result.get(11));
        //设置上课周数
        String week=list_result.get(8);

        if(week.contains("-")){
            Log.d("week","jinlaile");
            bean.setWeekfrom(Integer.parseInt(week.split("-")[0]));

            String[] weekTos=week.split("-")[1].split("");

            String weekTo="";
            label:for(int a=0;a<weekTos.length;a++){
                if(weekTos[a].equals("周")){
                    break label;
                }else{
                    weekTo+=weekTos[a];
                }
            }

            bean.setWeekto(Integer.parseInt(weekTo));

            bean.setType("no");

        }else if(week.contains(",")){
            String[] weekTos=week.split(",");
            weekTos[weekTos.length-1]=(weekTos[weekTos.length-1]).length()==4?weekTos[weekTos.length-1].substring(0,2):weekTos[weekTos.length-1].substring(0,1);
            bean.setWeekfrom(Integer.parseInt(weekTos[0]));
            bean.setWeekto(Integer.parseInt(weekTos[weekTos.length-1]));

            //判断单双周课程
            bean.setType(bean.getWeekfrom()%2!=0?"dan":"shuang");
        }

        //设置上课日期
        bean.setDay(Integer.parseInt(list_result.get(9)));
        Log.d("error","!!!!!!"+list_result.get(10));
        bean.setFrom(Integer.parseInt(list_result.get(10))*2-1);
        bean.setTo(Integer.parseInt(list_result.get(10))*2);

        return bean;
    }
}
