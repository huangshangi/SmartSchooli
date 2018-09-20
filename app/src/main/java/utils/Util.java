package utils;

import android.app.ActionBar;
import android.app.Activity;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.smartschool.smartschooli.R;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import bean.Class_Bean;

//工具类
public class Util {

    private static Util util;

    private Util(){

    }


    public static Util getInstance(){
        if(util==null){
            synchronized (Util.class){
                if(util==null){
                    util=new Util();
                }
            }
        }
        return util;
    }


    //MD5 32位加密
    public  static String getMD5Str(String str)
    {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++)
        {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    //将原生list<Class_Bean>转化为每周专属的list
    //list->原生list  week->当前要显示的周数
    public ArrayList<Class_Bean> getRealList(List<Class_Bean>list,int week){

        ArrayList<Class_Bean> result_list=new ArrayList<>();

        if(list!=null) {
            for (Class_Bean bean : list) {

                //判断课程是否在规定时间内
                Log.d("getRealList", "当前周" + week + "}" + bean.getWeekfrom() + ")" + bean.getWeekto());
                if (bean.getWeekfrom() <= week && bean.getWeekto() >= week) {
                    //单双周课程判断
                    if (bean.getType().equals("dan") && week % 2 != 0) {
                        result_list.add(bean);
                    } else if (bean.getType().equals("shuang") && week % 2 == 0) {
                        result_list.add(bean);
                    } else {
                        result_list.add(bean);
                    }
                }
            }
        }
        Log.d("result_list",result_list.size()+")))");
        return result_list;
    }


    //将dp转化为px
    public int  dp2px(int dp){
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    //将px转化为dp
    public int  px2dp(int px){
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    //屏幕变亮
    public void lightOn(Activity activity){
        WindowManager.LayoutParams params=activity.getWindow().getAttributes();
        params.alpha=1.0f;
        activity.getWindow().setAttributes(params);
    }

    //屏幕变暗
    public void lightOff(Activity activity){
        WindowManager.LayoutParams params=activity.getWindow().getAttributes();
        params.alpha=0.3f;
        activity.getWindow().setAttributes(params);
    }

    //得到当前所处周数
    public int getCurrentWeek(){
        Calendar calendar=Calendar.getInstance();

        calendar.setTime(new Date());


        int currentWeek=calendar.get(Calendar.WEEK_OF_YEAR)-getWeek(2018,9,10);


        if(currentWeek<1){
            currentWeek=1;
        }

        return currentWeek;
    }

    //得到今天是周几   周日 ->7
    public int getCurrentDay(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int result=calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(result==0){
            return 7;
        }else{
            return result;
        }
    }
    //得到某个日期所处周数
    private int getWeek(int year,int month,int day){
        Calendar calendar1=Calendar.getInstance();

        calendar1.set(Calendar.YEAR,year);

        calendar1.set(Calendar.MONTH,month-1);

        calendar1.set(Calendar.DAY_OF_MONTH,day);

        return calendar1.get(Calendar.WEEK_OF_YEAR);
    }

    //根据周数 周几得到当前所处日期
    public String getCurrentDay(int year,int week,int day){
        String date="";


        Calendar calendar=Calendar.getInstance();

        calendar.set(Calendar.YEAR,year);

        calendar.set(Calendar.WEEK_OF_YEAR,week+getWeek(2018,9,10)-1);

        calendar.set(Calendar.DAY_OF_WEEK,day);

        int month=calendar.get(Calendar.MONTH)+1;

        int day2=calendar.get(Calendar.DAY_OF_MONTH);

        if(month/10==0){
            date+="0"+month;
        }else{
            date+=month;
        }

        if(day2/10==0){
            date+="-0"+day2;
        }else{
            date+="-"+day2;
        }

        return date;
    }


    public Notification getNotification(){
        Notification notification=new NotificationCompat.Builder(MyApplication.getContext()).setContentTitle("校园网故障").setContentText("小管网发生故障")
        .setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.icon).setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(),R.mipmap.icon)).build();
        return notification;
    }
    /*得到当前处于第几节课
    *   假设上课时间
    *   1-2  08:00-09:50
    *   3-4  10:00-11:50
    *   5-6  13:00-14:50
    *   7-8  15:00-16:50
    *   9-10 18:00-19:50
     */
    public int getClassNumber(){

        Calendar calendar=Calendar.getInstance();

        calendar.setTime(new Date());

        int hour=calendar.get(Calendar.HOUR_OF_DAY);

        int minute=calendar.get(Calendar.MINUTE);

        int second=calendar.get(Calendar.SECOND);

        if(hour<8||hour>=20){
            return 0;
        }else if(hour>=8&&hour<10){
            if(hour==9&&minute>50){
                return 0;
            }else if(hour==8) {
                return 1;
            }else{
                return 2;
            }
        }else if(hour>=10&&hour<12){
            if(hour==11&&minute>50){
                return 0;
            }else if(hour==10) {
                return 3;
            }else{
                return 4;
            }
        }else if(hour>=13&&hour<15){
            if(hour==14&&minute>50){
                return 0;
            }else if(hour==13) {
                return 5;
            }else{
                return 6;
            }
        }else if(hour>=15&&hour<17){
            if(hour==16&&minute>50){
                return 0;
            }else if(hour==15) {
                return 7;
            }else{
                return 8;
            }
        }else if(hour>=18&&hour<20){
            if(hour==19&&minute>50){
                return 0;
            }else if(hour==18) {
                return 9;
            }else{
                return 10;
            }
        }

        return 0;
    }

    //得到当前时间  返回格式 08:00:00
    public String getCurrentTime(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());

        String result="";
        int hour=calendar.get(Calendar.HOUR_OF_DAY);

        int minute=calendar.get(Calendar.MINUTE);

        int second=calendar.get(Calendar.SECOND);

        if(hour/10==0){
            result+="0"+hour+":";
        }else{
            result+=hour+":";
        }

        if(minute/10==0){
            result+="0"+minute+":";
        }else{
            result+=minute+":";
        }

        if(second/10==0){
            result+="0"+second;
        }else{
            result+=second;
        }

        return result;
    }


    //进行权限的申请
    public  boolean requestPremission(Activity activity,String permission,int code){

        if(ContextCompat.checkSelfPermission(activity,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{permission},code);
        }
        return false;
    }

    public void requestPremissions(Activity activity,List<String>list_require,int code){

        List<String>list_reject=new ArrayList<String>();//储存被拒绝的权限
        for(int i=0;i<list_require.size();i++){
            if(ContextCompat.checkSelfPermission(activity,list_require.get(i))!= PackageManager.PERMISSION_GRANTED){
                list_reject.add(list_require.get(i));
            }
        }
        if(list_reject.size()!=0) {
            String[] array = new String[list_reject.size()];
            list_reject.toArray(array);
            //申请权限
            ActivityCompat.requestPermissions(activity, array, code);
        }
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    //type==0->无网络  type==1->wifi   type==2->流量
    public static int getApnType(Context context){
        int type=0;
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return type;
        }
        type=networkInfo.getType();
        if(type==ConnectivityManager.TYPE_WIFI){
            type=1;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            type=2;
        }

        return type;
    }

    //获取当前所连接的网络名称
    public static String getNetWorkName(Context context){
        if(isWifi(context)){
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int wifiState = wifiMgr.getWifiState();
            WifiInfo info = wifiMgr.getConnectionInfo();
            String wifiId = info != null ? info.getSSID() : null;
            return wifiId.substring(1,wifiId.length()-1);
        }else if (isLiuLiang(context)){
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            return telephonyManager.getSimOperatorName();
        }else{
            return "无";
        }
    }

    private static boolean isWifi(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo!=null){
            return networkInfo.isAvailable();
        }
        return false;
    }

    private static boolean isLiuLiang(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo!=null){
            return networkInfo.isAvailable();
        }
        return false;
    }
}
