package utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        for(Class_Bean bean:list){

            //判断课程是否在规定时间内
            Log.d("getRealList","当前周"+week+"}"+bean.getWeekfrom()+")"+bean.getWeekto());
            if(bean.getWeekfrom()<=week&&bean.getWeekto()>=week){
                //单双周课程判断
                if(bean.getType().equals("dan")&&week%2!=0){
                    result_list.add(bean);
                }else if(bean.getType().equals("shuang")&&week%2==0){
                    result_list.add(bean);
                }else {
                    result_list.add(bean);
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
}