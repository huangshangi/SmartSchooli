package utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
    public List<Class_Bean> getRealList(List<Class_Bean>list,int week){

        List<Class_Bean> result_list=new ArrayList<>();

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
}
