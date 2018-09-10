package utils;

import android.Manifest;

import android.app.Activity;
import android.app.Dialog;
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
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.smartschool.smartschooli.MainActivity;
import com.smartschool.smartschooli.PublishActivity;
import com.smartschool.smartschooli.R;
import com.smartschool.smartschooli.Submit_RepairActivity;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


import bean.Class_Bean;
import bean.Image_Bean;
import bean.Message_Bean;
import bean.Person_Bean;
import bean.Qiandao_Bean;
import bean.Repair_Bean;
import bean.Shuoshuo_Bean;
import bean.UpdateMessage_Bean;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import listener.Fragment_class_Listener;
import listener.Fragment_class_getQiandao_listener;
import listener.LoadingListener;
import listener.LoadingOwnRepairListener;
import listener.LoadingRepairListener;
import listener.Message_Down_Listener;
import listener.Publish_Shuoshuo_Listener;
import listener.UpMessage_Listener;
import listener.UpRepairListener;
import listener.UpdateMessageListener;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import view.LoadingProgress;

import static android.content.Context.TELEPHONY_SERVICE;

/*
    采用线程池
    建议与网络有关的操作都在该类中进行
    使用方法：
        NetWorkLoader.getInstance()获取一个NetworkLoader实例
        当需要执行一个任务时调用addTask(Runnable runable)将任务添加至队列，并在任务执行完毕后调用semaphore.release()方法


 */

public class NetworkLoader {

    int count=0;

    private String lastQueryTime;//条件查询聊天记录时上一次查询时间

    private String phone_id;//手机唯一码

    final static int THREAD_COUNT=3;//线程数

    static NetworkLoader networkLoader;

    private ArrayList<Class_Bean>list_bean;//盛放一周课程信息

    private MediaPlayer mediaPlayer;

    private ExecutorService mThreadPool;//线程池

    private LinkedList<Runnable>linkedList;//线程池任务队列

    private Handler mUiHandler;//UI更新handler


    private Handler mTaskHandler;//负责通知线程池有新任务加入

    private Thread thread;//后台轮询线程

    private Semaphore semaphore_getList;//判断是否成功获取课程信息

    private Semaphore semaphore;//每执行完一个任务时会释放一个信号

    private Semaphore s;//该信号量标记mHandlerPool初始化完毕

    private Semaphore semaphore_fileUp;

    private static Map<Integer,List<Class_Bean>>hashMap;//储存课程信息 单例

    private Fragment_class_Listener fragment_class_listener;

    private Fragment_class_getQiandao_listener fragment_class_getQiandao_listener;

    private UpRepairListener upRepairListener;

    private LoadingRepairListener loadingRepairListener;

    private LoadingOwnRepairListener loadingOwnRepairListener;

    private UpdateMessageListener updateMessageListener;

    private LoadingListener loadingListener;

    private Publish_Shuoshuo_Listener publish_shuoshuo_listener;

    private Message_Down_Listener message_down_listener;

    private UpMessage_Listener upMessage_listener;

    private List<String>list_urls;

    public void setUpMessage_listener(UpMessage_Listener upMessage_listener) {
        this.upMessage_listener = upMessage_listener;
    }

    public UpMessage_Listener getUpMessage_listener() {
        return upMessage_listener;
    }

    public void setMessage_down_listener(Message_Down_Listener message_down_listener) {
        this.message_down_listener = message_down_listener;
    }

    public Message_Down_Listener getMessage_down_listener() {
        return message_down_listener;
    }

    public void setPublish_shuoshuo_listener(Publish_Shuoshuo_Listener publish_shuoshuo_listener) {
        this.publish_shuoshuo_listener = publish_shuoshuo_listener;
    }

    public Publish_Shuoshuo_Listener getPublish_shuoshuo_listener() {
        return publish_shuoshuo_listener;
    }

    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }

    public LoadingListener getLoadingListener() {
        return loadingListener;
    }

    public void setLoadingOwnRepairListener(LoadingOwnRepairListener loadingOwnRepairListener) {
        this.loadingOwnRepairListener = loadingOwnRepairListener;
    }

    public void setUpdateMessageListener(UpdateMessageListener updateMessageListener) {
        this.updateMessageListener = updateMessageListener;
    }

    public void setLoadingRepairListener(LoadingRepairListener loadingRepairListener) {
        this.loadingRepairListener = loadingRepairListener;
    }

    public void setUpRepairListener(UpRepairListener upRepairListener) {
        this.upRepairListener = upRepairListener;
    }

    public void setFragment_class_getQiandao_listener(Fragment_class_getQiandao_listener fragment_class_getQiandao_listener) {
        this.fragment_class_getQiandao_listener = fragment_class_getQiandao_listener;
    }

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

        //初始化缓冲池
        int memory=(int)Runtime.getRuntime().maxMemory();
        int cachememory=memory/6;


        mThreadPool= Executors.newFixedThreadPool(THREAD_COUNT);

        linkedList=new LinkedList<>();

        semaphore_fileUp=new Semaphore(0);

        semaphore=new Semaphore(THREAD_COUNT);

        semaphore_getList=new Semaphore(0);

        s=new Semaphore(0);

        list_urls=new ArrayList<>();

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
    public synchronized  void addTask(Runnable runnable){
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
                            Log.d("kind测试",person_bean.getKind());
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
                if(type.equals("主管")){
                    bean.signUp(new SaveListener<Person_Bean>() {
                        @Override
                        public void done(Person_Bean person_bean, BmobException e) {
                            if(e==null){
                                //注册成功
                                activity.finish();
                            }else{
                                Toast.makeText(activity,"注册失败,"+s,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    verfiyZhanghao(bean, pass, activity);
                }


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

    //更新用户头像
    public void updateImage(final String address){

        addTask(new Runnable() {
            @Override
            public void run() {
                count=0;
                upFile(address,1);
                try {
                    semaphore_fileUp.acquire();
                }catch (Exception e){

                }
                SharedPreferences.Editor editor=MyApplication.getContext().getSharedPreferences("Person_Data", Context.MODE_PRIVATE).edit();
                editor.putString("image",list_urls.get(0));
                editor.apply();
                update_Image2(list_urls.get(0));
                list_urls.clear();
                semaphore.release();
            }
        });

    }

    //在user表中更新个人头像
    private void update_Image2(final String url){
        addTask(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Person_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.addWhereEqualTo("username",getPersonMessage().get(0));
                bmobQuery.findObjects(new FindListener<Person_Bean>() {
                    @Override
                    public void done(List<Person_Bean> list, BmobException e) {
                        if(e==null&&list.size()!=0){
                            Person_Bean bean=list.get(0);
                            bean.setImage(url);
                            bean.update(bean.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e!=null)
                                        Toast.makeText(MyApplication.getContext(),"更新用户头像失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    //获取某一节课签到信息
    public void getQiandao(final String CNQ, final String courseName, final String week, final String day, final String jieshu){
        addTask(new Runnable() {
            @Override
            public void run() {

                BmobQuery<Qiandao_Bean>beanBmobQuery=new BmobQuery<>();

                beanBmobQuery.addWhereEqualTo("cNQ",CNQ);
                beanBmobQuery.addWhereEqualTo("courseNumber",courseName);
                beanBmobQuery.addWhereEqualTo("week",week);
                beanBmobQuery.addWhereEqualTo("day",day);
                beanBmobQuery.addWhereEqualTo("jieshu",jieshu);

                beanBmobQuery.findObjects(new FindListener<Qiandao_Bean>() {
                    @Override
                    public void done(List<Qiandao_Bean> list, BmobException e) {
                        if(e==null){
                            //回调方法

                        }else{
                            Toast.makeText(MyApplication.getContext(),"签到信息获取失败"+s,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                semaphore.release();
            }
        });

    }

    //上传签到信息
    public void UpQiandao(final String cNQ, final String courseName, final String week, final String day,final String jieshu,final String name, final String xuehao){

        addTask(new Runnable() {
            @Override
            public void run() {
                Qiandao_Bean bean=new Qiandao_Bean();
                bean.setcNO(cNQ);
                bean.setCourseNumber(courseName);
                bean.setDay(day);
                bean.setWeek(week);
                bean.setJieshu(jieshu);
                bean.setName(name);
                bean.setXuehao(xuehao);
                bean.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(MyApplication.getContext(),"签到成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MyApplication.getContext(),"签到失败"+s,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                semaphore.release();
            }
        });

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
                if(list.get(3).equals("教师")){
                    username="201700301242";
                }else if(list.get(3).equals("学生")&&list.get(0).equals("S1")){
                    username="201700301242";
                }else if(list.get(3).equals("主管")){
                    semaphore.release();
                    return;
                }


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

    public void prepareRepairMessage(final String id,final String nickname,final String machine,final String type,final String address,final int bianhao,final String content,final List list,final Context context) {

        final Dialog dialog= LoadingProgress.createDialog(context,"加载中...");
        //将图片上传
        list_urls.clear();
        final List list1 = new ArrayList();


        for(int i=1;i<list.size();i++)
            list1.add(list.get(i));
        Log.d("semaphore_fileUp","list"+list.size());

        addTask(new Runnable() {
            @Override
            public void run() {
                upFiles(list1);
                try {
                    semaphore_fileUp.acquire();
                } catch (Exception e) {

                }
                // 图片上传成功,将信息组装
                Repair_Bean bean = new Repair_Bean();
                bean.setRepair_type(type);
                bean.setRepairer_id(id);
                bean.setRepair_machine(machine);
                bean.setRepairer_name(nickname);
                bean.setEvluate_content("");
                bean.setEvluate_status("否");
                //将返回的url地址组装成String 中间用￥间隔
                String urls = "";
                for (int i = 0; i < list_urls.size(); i++) {
                    urls += list_urls.get(i);

                    if (i != list_urls.size() - 1) {
                        urls += "￥";
                    }
                }
                bean.setRepair_adress(address);
                bean.setRepair_urls(urls);
                Log.d("测试信息！！",urls+"!");
                bean.setRepair_content(content);
                bean.setRepair_status("未处理");
                bean.setRepair_bianhao(bianhao);
                upRepairMessage(bean,context);

                semaphore.release();
                dialog.dismiss();
            }
        });
        Submit_RepairActivity.list_all.clear();

    }


    //上传维修信息
    public void upRepairMessage(Repair_Bean bean, final Context context){

        bean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(context,"维修信息上传成功",Toast.LENGTH_LONG).show();
                    if(upRepairListener!=null){
                        upRepairListener.upDown();
                    }
                    getRepairMessage();
                }else{
                    Toast.makeText(context,"维修上传失败,请稍后重试",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //得到维修信息
    public void getRepairMessage(){
        addTask(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Repair_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.order("-createdAt");
                bmobQuery.findObjects(new FindListener<Repair_Bean>() {
                    @Override
                    public void done(List<Repair_Bean> list, BmobException e) {
                        if(e==null){
                            //查询成功，回调方法
                            if(loadingRepairListener!=null){
                                loadingRepairListener.loadingDown(list);
                            }
                        }
                    }
                });

                semaphore.release();
            }
        });
    }

    //获取我提交的维修内容
    public void getOwnRepairMessage(){
        addTask(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Repair_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.addWhereEqualTo("repairer_id",getPersonMessage().get(0));
                bmobQuery.order("-createdAt");
                bmobQuery.findObjects(new FindListener<Repair_Bean>() {
                    @Override
                    public void done(List<Repair_Bean> list, BmobException e) {
                        if(e==null){
                            //查询成功，回调方法
                          if(loadingOwnRepairListener!=null){
                              loadingOwnRepairListener.loadingDown(list);
                          }
                        }
                    }
                });

                semaphore.release();
            }
        });
    }


    //上传多个文件
    private void upFiles(List<String>list){

        count=0;
        int count_zong=list.size();
        if(list==null||list.size()==0){
            semaphore_fileUp.release();
        }else {
            for (int i = 0; i < count_zong; i++) {
                checkFileUp(list.get(i), count_zong);
            }
        }
    }
    //根据文件在sd卡的路径得到文件名
    private String getName(String address){
        int lastIndex=address.lastIndexOf("/");
        String name=address.substring(lastIndex);
        return name;
    }

    //上传单个文件,将内存卡中address
    private void upFile(final String address,final int count_zong){
        DisplayMetrics displayMetrics=MyApplication.getContext().getResources().getDisplayMetrics();
        int reqHeight=displayMetrics.heightPixels/5;
        int reqWidth=displayMetrics.widthPixels/3;

        //组装ImageSize
        ImageSize imageSize=new ImageSize();
        imageSize.height=reqHeight;
        imageSize.width=reqWidth;

        //将图片压缩(尺寸压缩)
        Bitmap bitmap=decodeImage(address,imageSize);

        //质量压缩

        Bitmap bitmap2=decodeImage2(bitmap);
        //将Bitmap转化为File对象
        final File file=BitmapToFile(bitmap2,getName(address));

        //开始上传
        addTask(new Runnable() {
            @Override
            public synchronized void run() {
                final BmobFile bmobFile=new BmobFile(file);

                Log.d("测试信息！！","文件开始上传");
                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {

                        if(e==null){
                            //文件上传成功,将文件对应关系在数据库中更新
                            Log.d("测试信息！！","文件上传成功");
                            Toast.makeText(MyApplication.getContext(),"文件上传成功",Toast.LENGTH_SHORT).show();
                            list_urls.add(bmobFile.getFileUrl());
                            UpdateUrl(address,bmobFile.getFileUrl());

                            Log.d("文件上传成功，url的地址为：","!!!!!!!!"+bmobFile.getFileUrl()+"!!"+list_urls.size());

                            if(++count==count_zong){
                                semaphore_fileUp.release();
                                Log.d("semaphore_fileUp","文件上传时被释放");
                            }
                        }else{
                            Log.d("测试信息！！","文件上传失败"+e.getMessage());
                            Toast.makeText(MyApplication.getContext(),"文件上传失败，错误信息:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        semaphore.release();
                    }
                });
            }
        });


    }
    private Bitmap decodeImage(String address, ImageSize imageSize){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(address,options);

        //计算压缩比例
        options.inSampleSize=calculateBiLi(imageSize,options);
        options.inJustDecodeBounds=false;

        return BitmapFactory.decodeFile(address,options);
    }


    private Bitmap decodeImage2(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int o = 25;
            while (baos.toByteArray().length / 1024 > 20&&o>10) { // 循环判断如果压缩后图片是否大于10kb,大于继续压缩
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, o, baos);
                 o -= 10;// 每次都减少10
            }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap1;
    }

    //imageSize代表要求的大小  option代表实际的大小
    private int calculateBiLi(ImageSize imageSize, BitmapFactory.Options options){
        int SampleSize=1;
        int reqWidth=imageSize.width;
        int reqHeight=imageSize.height;
        int realWidth=options.outWidth;
        int realHeight=options.outHeight;
        if(realHeight>reqHeight||realWidth>reqWidth){
            int widthRadio=realWidth/reqWidth;
            int heightRadio=realHeight/reqHeight;
            SampleSize=Math.max(widthRadio,heightRadio);
        }
        return SampleSize;
    }



    //将Bitmap转化为File对象
    public File BitmapToFile(Bitmap bitmap,String name){
        File file=new File(MyApplication.getContext().getExternalCacheDir(),name);
        Log.d("文件名：",MyApplication.getContext()+"!!!!!!!"+name);
        try{
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return file;
        }catch (Exception e){

        }
        return file;
    }


    //检查该文件是否已经上传,防止重复上传
    private void checkFileUp(final String address, final int count_zong){
        addTask(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Image_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.addWhereEqualTo("phone_id",phone_id);
                bmobQuery.addWhereEqualTo("image_sd_url",address);
                bmobQuery.findObjects(new FindListener<Image_Bean>() {
                    @Override
                    public void done(List<Image_Bean> list, BmobException e) {
                        if(list!=null&&list.size()!=0){
                            //该文件已经上传,得到已经上传的图片url地址
                            Log.d("文件已经上传","true");
                            getUrlAccordSd(address,count_zong);


                        }else{
                            //该文件未被上传
                            Toast.makeText(MyApplication.getContext(),"该文件未被上传",Toast.LENGTH_SHORT).show();
                            upFile(address,count_zong);
                        }
                        semaphore.release();
                    }
                });

            }
        });
    }


    private void getUrlAccordSd(final String address,final int count_zong){
        addTask(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Image_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.addWhereEqualTo("image_sd_url",address);
                bmobQuery.addWhereEqualTo("phone_id",phone_id);
                bmobQuery.findObjects(new FindListener<Image_Bean>() {
                    @Override
                    public void done(List<Image_Bean> list, BmobException e) {
                        if(e==null){
                            list_urls.add(list.get(0).getImage_inet_url());
                            Toast.makeText(MyApplication.getContext(),"路径"+list.get(0).getImage_inet_url(),Toast.LENGTH_SHORT).show();
                        }else{

                            Toast.makeText(MyApplication.getContext(),"查询数据失败，失败原因："+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        if(++count==count_zong){
                            Log.d("semaphore_fileUp","从inet查询时被释放");
                            semaphore_fileUp.release();
                        }
                        semaphore.release();
                    }
                });

            }
        });
    }

    //某条消息已读
    public void messageHasRead(final String target_id,final String own_id){
        addTask(new Runnable() {
            @Override
            public void run() {
                BmobQuery<UpdateMessage_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.addWhereEqualTo("update_receiverId",own_id);
                bmobQuery.addWhereEqualTo("update_senderId",target_id);
                bmobQuery.findObjects(new FindListener<UpdateMessage_Bean>() {
                    @Override
                    public void done(List<UpdateMessage_Bean> list, BmobException e) {
                        if (e == null && list.size() > 0) {
                            UpdateMessage_Bean bean = list.get(0);
                            bean.setHasread(true);
                            bean.update(bean.getObjectId(),new UpdateListener() {
                                @Override
                                public void done(BmobException e) {

                                }
                            });
                        }else{
                            Log.d("errror rrr","未查到");
                        }
                    }

                });

                semaphore.release();
            }
        });

    }


    //获取最新聊天记录，并回调
    public void getUpdateMessage(){
        addTask(new Runnable() {
            @Override
            public void run() {
                List<String>list3=getPersonMessage();
                BmobQuery<UpdateMessage_Bean>bmobQuery1=new BmobQuery<>();
                bmobQuery1.addWhereEqualTo("update_senderId",list3.get(0));
                BmobQuery<UpdateMessage_Bean>bmobQuery2=new BmobQuery<>();
                bmobQuery2.addWhereEqualTo("update_receiverId", list3.get(0));
                List<BmobQuery<UpdateMessage_Bean>>list=new ArrayList<>();
                list.add(bmobQuery1);
                list.add(bmobQuery2);
                BmobQuery<UpdateMessage_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.or(list);
                bmobQuery.findObjects(new FindListener<UpdateMessage_Bean>() {
                    @Override
                    public void done(List<UpdateMessage_Bean> list, BmobException e) {
                        if(e==null){
                            //查询成功,回调
                            if(updateMessageListener!=null){
                                updateMessageListener.getUpdateMessage(list);
                            }
                        }
                    }
                });
                semaphore.release();
            }
        });

    }



    //将该文件在sd卡中的位置与在inet上的位置对应
    private void UpdateUrl(String address,String url){
        Image_Bean bean=new Image_Bean();
        bean.setImage_inet_url(url);
        bean.setImage_sd_url(address);
        bean.setPhone_id(phone_id);
        bean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){

                }else{
                    Toast.makeText(MyApplication.getContext(), "更新Image数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //发表说说操作，其中publisher_name，publisher_image从本地获取  commit默认为空
    public void publish_Shuoshuo(final String content, final List<String>list, final Context context){
        //启动加载框
        PublishActivity.list_all.clear();
        final Dialog dialog=LoadingProgress.createDialog(context,"加载中...");
        list_urls.clear();
        upFiles(list);

        addTask(new Runnable() {

            @Override
            public void run() {
                //如果文件未上传成功，则一直等待
                List<String>list=getPersonMessage();
                try {

                    semaphore_fileUp.acquire();
                    Toast.makeText(MyApplication.getContext(),"等待完成",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("errror occur", e.getMessage());
                }

                Shuoshuo_Bean bean = new Shuoshuo_Bean();
                bean.setPublisher_id(list.get(0));
                bean.setPublished_commit("");
                bean.setPublished_content(content);
                bean.setPublisher_name(list.get(2));
                bean.setPublisher_url(list.get(1));

                //将返回的url地址组装成String 中间用￥间隔
                String urls = "";
                for (int i = 0; i < list_urls.size(); i++) {
                    urls += list_urls.get(i);

                    if (i != list_urls.size() - 1) {
                        urls += "￥";
                    }
                }
                Log.d("url的地址为：","!!!!!!!!"+urls);
                bean.setPublished_urls(urls);
                bean.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                        if (e == null) {

                            //说说上传成功,回调接口
                            if (publish_shuoshuo_listener != null) {
                                publish_shuoshuo_listener.Publish_ShuoshuoDown();
                            }
                        } else {

                            Toast.makeText(MyApplication.getContext(), "说说上传失败，错误原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                semaphore.release();
                //说说上传完毕，加载框消失
                dialog.dismiss();
                getShuoshuo();
            }
        });

    }

    //获得说说
    public void getShuoshuo(){
        BmobQuery<Shuoshuo_Bean>bmobQuery=new BmobQuery<>();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<Shuoshuo_Bean>() {
            @Override
            public void done(List<Shuoshuo_Bean> list, BmobException e) {
                if(e==null){
                    //查询成功，回调方法
                    if(loadingListener!=null){
                        loadingListener.loadingDown(list);
                    }
                }else{
                    Toast.makeText(MyApplication.getContext(),"获取说说失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //添加评论
    public void addCommit(final Shuoshuo_Bean bean){
        addTask(new Runnable() {
            @Override
            public void run() {
                bean.update(bean.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e!=null){
                            Toast.makeText(MyApplication.getContext(),"发表评论失败,"+"失败原因"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        semaphore.release();
                    }
                });
            }
        });
    }


    //根据发送人，接受者获取消息
    public void getMessage(final String publisher_id,final String receiver_id,final int i){
        addTask(new Runnable() {
            @Override
            public void run() {
                String[]array={publisher_id,receiver_id};
                List list= Arrays.asList(array);
                BmobQuery<Message_Bean>bmobQuery=new BmobQuery<>();
                bmobQuery.order("-createdAt");
                bmobQuery.addWhereContainedIn("sender_id",list);
                bmobQuery.addWhereContainedIn("receiver_id",list);
                if(i!=0){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date  = null;
                    try {
                        date = sdf.parse(lastQueryTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    bmobQuery.addWhereLessThanOrEqualTo("createdAt",new BmobDate(date));
                    Log.d("&&&&","回调方法被执行");
                }
                bmobQuery.setLimit(15);
                bmobQuery.findObjects(new FindListener<Message_Bean>() {
                    @Override
                    public void done(List<Message_Bean> list, BmobException e) {
                        if(e==null){
                            //查询成功，回调方法

                            if(message_down_listener!=null){
                                if(list.size()>0)
                                    lastQueryTime=list.get(list.size()-1).getCreatedAt();
                                for(int i=0;i<list.size();i++)
                                    Log.d("&&&&&&&&&&&&&",list.get(i).getContent()+"!!!!!"+list.size());

                                message_down_listener.getMessageDown(list);
                            }
                        }else{
                            Toast.makeText(MyApplication.getContext(),"服务器获取信息失败；"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                semaphore.release();
            }
        });

    }


    //播放音乐,type类型用来标记播放那一边的音乐,1代表右边，2代表左边
    public void playMedia(String address, final View view, final int type){

        try {

            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(MyApplication.getContext(), Uri.parse(address));
            mediaPlayer.prepare();
            mediaPlayer.start();

            //正常结束
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(type==2)
                        view.setBackgroundResource(R.drawable.adj_left);
                    else
                        view.setBackgroundResource(R.drawable.adj_right);
                }
            });
        }catch (Exception e){

        }

    }

    //发送消息
    public  void addMessage(final String publisher_id,final String receiver_id,final String imageurl,final String type,final String content){
        addTask(new Runnable() {
            @Override
            public synchronized void run() {
                Message_Bean bean=new Message_Bean();
                bean.setContent(content);
                bean.setImage(imageurl);
                bean.setReceiver_id(receiver_id);
                bean.setSender_id(publisher_id);
                bean.setType(type);

                if(type.equals("SOUND")){
                    upChatFile(bean,MyApplication.getContext().getExternalCacheDir()+"/ceshisound.amr");
                }else if(type.equals("MESSAGE")){
                    bean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                //发送成功
                            } else {
                                Toast.makeText(MyApplication.getContext(), "发送信息失败:" + s, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else if(type.equals("IMAGE")){
                    upChatFile(bean,content);
                }
                semaphore.release();
                if(upMessage_listener!=null){
                    upMessage_listener.MessageDown(bean);
                }
                addUpdateMessage(bean);
            }
        });

    }

    /*发送最新聊天记录
          参数Message_Bean代表一条消息记录，其中receiver_id，type,content,publisher_id可以使用
     */
    private void addUpdateMessage(final Message_Bean bean){

        addTask(new Runnable() {
            @Override
            public void run() {

                //根据id查询昵称，头像地址
                BmobQuery<Person_Bean> bmobQuery=new BmobQuery<>();
                bmobQuery.addWhereEqualTo("username",bean.getReceiver_id());

                bmobQuery.findObjects(new FindListener<Person_Bean>() {
                    @Override
                    public void done(List<Person_Bean> list, BmobException e) {
                        if(e==null){

                            //查询昵称，头像地址成功,将信息组装
                            final Person_Bean bean1=list.get(0);

                            //查询表中是否两用户间存在最新消息记录，if true 更新 else 添加
                            String[]array={bean.getSender_id(),bean.getReceiver_id()};
                            List l= Arrays.asList(array);
                            BmobQuery<UpdateMessage_Bean>bmobQuery1=new BmobQuery<>();
                            bmobQuery1.addWhereContainedIn("update_senderId",l);
                            bmobQuery1.addWhereContainedIn("update_receiverId",l);

                            bmobQuery1.findObjects(new FindListener<UpdateMessage_Bean>() {
                                @Override
                                public void done(List<UpdateMessage_Bean> list, BmobException e) {
                                    if(e==null){
                                        Log.d("执行该信息","查询成功");
                                        //存在消息记录
                                        UpdateMessage_Bean updateMessage_bean=list.get(0);
                                        updateMessage_bean.setImageUrl(bean1.getImage());
                                        updateMessage_bean.setUpdate_senderId(bean.getSender_id());
                                        updateMessage_bean.setUpdate_receiverId(bean.getReceiver_id());
                                        updateMessage_bean.setUpdate_content(bean.getContent());
                                        updateMessage_bean.setUpdate_type(bean.getType());
                                        updateMessage_bean.setUpdate_receiver_name(bean1.getNickname());
                                        updateMessage_bean.setUpdate_sender_name(getPersonMessage().get(2));
                                        updateMessage_bean.setHasread(false);
                                        updateMessage_bean.update(updateMessage_bean.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e!=null){
                                                    Toast.makeText(MyApplication.getContext(),"更新失败，"+e.getMessage(),Toast.LENGTH_SHORT).show();

                                                }else{

                                                }
                                            }
                                        });
                                    }else{
                                        Log.d("执行该信息","查询失败");
                                        UpdateMessage_Bean updateMessage_bean=new UpdateMessage_Bean();
                                        updateMessage_bean.setImageUrl(bean1.getImage());
                                        updateMessage_bean.setUpdate_senderId(bean.getSender_id());
                                        updateMessage_bean.setUpdate_receiverId(bean.getReceiver_id());
                                        updateMessage_bean.setUpdate_content(bean.getContent());
                                        updateMessage_bean.setUpdate_type(bean.getType());
                                        updateMessage_bean.setUpdate_receiver_name(bean1.getNickname());
                                        updateMessage_bean.setUpdate_sender_name(getPersonMessage().get(2));
                                        updateMessage_bean.setHasread(false);
                                        updateMessage_bean.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if(e!=null){
                                                    Toast.makeText(MyApplication.getContext(),"shangchuang失败，"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Log.d("执行该信息","异质该信息5");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }else{

                        }
                    }
                });
                semaphore.release();
            }

        });

    }

    //上传聊天文件
    private void upChatFile(final Message_Bean bean, final String filename){

        if(!filename.endsWith("amr")){
            count=0;
            list_urls.clear();
            checkFileUp(filename,1);
            try {
                semaphore_fileUp.acquire();
            }catch (Exception e){
                Log.d("上传文件类型","error occur");
            }
            Log.d("上传文件类型","上传图像文件");
            bean.setContent(list_urls.get(0));
            list_urls.clear();
            Log.d("上传文件类型",bean.getContent()+"!"+bean.getType());
            bean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        //发送成功
                    } else {
                        Toast.makeText(MyApplication.getContext(), "发送信息失败:" + s, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }else {
            addTask(new Runnable() {
                @Override
                public synchronized void run() {
                    final BmobFile bmobFile = new BmobFile(new File(filename));
                    bmobFile.upload(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //上传成功
                                bean.setContent(bmobFile.getFileUrl());
                                bean.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            //发送成功
                                        } else {
                                            Toast.makeText(MyApplication.getContext(), "发送信息失败:" + s, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                        }
                    });
                    semaphore.release();
                }
            });

        }
    }


    class ImageHolder{
        Bitmap bitmap;
        String url;
        ImageView imageView;
    }

    class ImageSize{
        int height;
        int width;
    }

}
