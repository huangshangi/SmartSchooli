package com.smartschool.smartschooli;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


import bean.NetWork;
import cn.bmob.v3.BmobUser;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.Fragment_class;
import fragments.Fragment_hall;

import fragments.Fragment_repair;
import listener.NetworkGuZhang_Listener;
import utils.ImageLoader;
import utils.NetworkLoader;
import utils.Util;


//主界面，内部有三个碎片
public class MainActivity extends AppCompatActivity{

    String id;
    String nickname;
    String image;
    View header;

    final static int REQUEST_PERMISSIONS=10;

    List<String> list_permission;//记录未被同意的权限



    List<Fragment> list;
    Fragment_hall fragment_hall;
    Fragment_class fragment_class;
    Fragment_repair fragment_repair;

    CircleImageView header_image;

    DrawerLayout drawerLayout;

    ListView listView;//侧滑菜单

    TextView textView_name;

    List<String>person_list;//储存个人信息的list

    //底部按钮
    RadioGroup radioGroup;
    RadioButton button_jiaoyi;
    RadioButton button_class;
    RadioButton button_repair;


    //滑动界面

    ListView navigationView;


    public DrawerLayout getDrawerLayout(){
        return drawerLayout;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //如果系统5.0以上
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.royalblue));
        }
        setContentView(R.layout.main_layout);
        initViews();
        initEvents();
        addListener();//添加监听器
        switchFragment(0);
    }


    //为控件添加监听器
    public void addListener(){


        //为退出登录按钮设立点击事件



        //为radioGroup添加点击事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.button_jiaoyi:
                        switchFragment(0);
                        break;
                    case R.id.button_class:
                        switchFragment(1);
                        break;
                    case R.id.button_repair:
                        switchFragment(2);
                        break;
                }
            }
        });

        //侧滑控件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //点击了用户头像
                        Intent intent=new Intent(MainActivity.this,PhotoSelector.class);
                        intent.putExtra("isOne","MainActivity");
                        startActivityForResult(intent,0x997);

                        break;
                    case 1:
                        //点击了账号资料
                        Intent intent2=new Intent(MainActivity.this,PersonActivity.class);
                        startActivity(intent2);
                        break;

                    case 2:
                        //点击了维修记录
                        Intent intent3=new Intent(MainActivity.this,RepairDetailsActivity.class);
                        startActivity(intent3);
                        break;
                    case 3:
                        //点击了聊天列表
                        startActivity(new Intent(MainActivity.this,Chat_List_Activity.class));
                        break;
                    case 4:
                       //点击了系统信息
                        startActivity(new Intent(MainActivity.this, LocationActivity.class));
                        break;
                    case 5:
                        //关于我们
                        startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
                        break;
                    case 6:
                        //退出登录
                        BmobUser.logOut();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        MainActivity.this.finish();
                        break;
                }
                drawerLayout.closeDrawers();
            }
        });


    }
    //初始化控件
    public void initViews(){

        list=new ArrayList<Fragment>();
        fragment_class=new Fragment_class();
        fragment_hall=new Fragment_hall();
        fragment_repair=new Fragment_repair();

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        button_class=(RadioButton)findViewById(R.id.button_class);
        button_jiaoyi=(RadioButton)findViewById(R.id.button_jiaoyi);
        button_repair=(RadioButton)findViewById(R.id.button_repair);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawlayout);
        navigationView=(ListView) findViewById(R.id.navigationView);

        listView=(ListView)findViewById(R.id.navigationView);

        header=LayoutInflater.from(this).inflate(R.layout.header_layout,null);

        header_image=(CircleImageView)header.findViewById(R.id.header_layout_image);
        textView_name=(TextView)header.findViewById(R.id.header_layout_name);



    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.closeDrawers();}
        else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 0x997:
                if(resultCode==RESULT_OK){
                    String address=data.getStringArrayListExtra("list").get(0);
                    ImageLoader.getInstance().loadImage(address,header_image);
                    //将内容在网络更新
                    NetworkLoader.getInstance().updateImage(address);
                }
                break;
        }
        //fragment_class 相应onActivityResult
        fragment_class.onActivityResult(requestCode,resultCode,data);
    }

    public void initEvents(){
        list.add(fragment_hall);
        list.add(fragment_class);
        list.add(fragment_repair);
        person_list=NetworkLoader.getInstance().getPersonMessage();




        ImageLoader.getInstance().loadImage(person_list.get(1),header_image);
        textView_name.setText(person_list.get(2));
        navigationView.setAdapter(new NavigationView_Adapter());
        navigationView.addHeaderView(header);



    }

    //为textView设置背景图片
    private void  setBack(int id,Button view){
        Drawable drawable = getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        // param 左上右下
        view.setCompoundDrawables(null,drawable,null,null);
    }

    //根据点击事件变换相应的fragment
    public void switchFragment(int position){
        if(position==0){
           setBack(R.drawable.hall_button_sele32,button_jiaoyi);
           setBack(R.drawable.class_button_32,button_class);
           setBack(R.drawable.repair_button32,button_repair);
        }else if(position==1){
            setBack(R.drawable.hall_button32,button_jiaoyi);
            setBack(R.drawable.class_button_sele32,button_class);
            setBack(R.drawable.repair_button32,button_repair);
        }else if(position==2){
            setBack(R.drawable.hall_button32,button_jiaoyi);
            setBack(R.drawable.class_button_32,button_class);
            setBack(R.drawable.repair_buttonsele32,button_repair);
        }
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            if (i == position) {
                if (list.get(i).isAdded()) {
                    transaction.show(list.get(i));
                } else {
                    transaction.add(R.id.framelayout, list.get(i));
                }
            } else {
                if (list.get(i).isAdded()) {
                    transaction.hide(list.get(i));
                }
            }

        }

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //该程序用到的所有权限
    public void requestPermissions(String[]permissions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_PERMISSIONS:
                if(grantResults!=null&&permissions!=null){
                    for(int i=0;i<permissions.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            list_permission.add(permissions[i]);
                        }
                    }
                }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //侧滑栏左侧，数据自己提供
    class NavigationView_Adapter extends BaseAdapter{

        List<String>list;

        public NavigationView_Adapter(){
            list=new ArrayList<>();
            list.add("账号资料");
            list.add("维修记录");
            list.add("聊天列表");
            list.add("系统信息");
            list.add("关于我们");
            list.add("退出登录");
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view==null){
                view=LayoutInflater.from(MainActivity.this).inflate(R.layout.navigation_item,viewGroup,false);
                viewHolder=new ViewHolder();
                viewHolder.textView=(TextView)view.findViewById(R.id.navigationView_item_text);
                view.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)view.getTag();
            }

            Drawable drawable=null;
            if(i==0){
                drawable=getResources().getDrawable(R.drawable.personn16);
            }else if(i==1){
                drawable=getResources().getDrawable(R.drawable.repair16);
            }else if(i==2){
                drawable=getResources().getDrawable(R.drawable.chatlist16);
            }else if(i==3){
                drawable=getResources().getDrawable(R.drawable.setting);
            }else if(i==4){
                drawable=getResources().getDrawable(R.drawable.aboutus16 );

            }else if(i==5){
                drawable=getResources().getDrawable(R.drawable.exit16);
            }

            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            viewHolder.textView.setCompoundDrawables(drawable,null,null,null);
            viewHolder.textView.setText((String)getItem(i));
            return view;
        }
    }
    class ViewHolder{
        TextView textView;
    }
}
