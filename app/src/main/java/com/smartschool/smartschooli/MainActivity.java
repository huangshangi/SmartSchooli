package com.smartschool.smartschooli;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


import cn.bmob.v3.BmobUser;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.Fragment_class;
import fragments.Fragment_hall;
import fragments.Fragment_repair;
import utils.ImageLoader;
import utils.MyApplication;
import utils.NetworkLoader;



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

    TextView textView_name;

    List<String>person_list;//储存个人信息的list

    //底部按钮
    RadioGroup radioGroup;
    RadioButton button_jiaoyi;
    RadioButton button_class;
    RadioButton button_repair;

    //退出登录按钮
    Button button_unsign;

    //滑动界面
    DrawerLayout drawerLayout;
    ListView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
        initViews();
        initEvents();
        addListener();//添加监听器
        switchFragment(0);
    }


    //为控件添加监听器
    public void addListener(){


        //为退出登录按钮设立点击事件
        button_unsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobUser.logOut();
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });


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
        button_unsign=(Button)findViewById(R.id.unsign_Button);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawlayout);
        navigationView=(ListView) findViewById(R.id.navigationView);

        header=LayoutInflater.from(this).inflate(R.layout.header_layout,null);

        header_image=(CircleImageView)header.findViewById(R.id.header_layout_image);
        textView_name=(TextView)header.findViewById(R.id.header_layout_name);



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


    //根据点击事件变换相应的fragment
    public void switchFragment(int position){
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
            list.add("请假记录");
            list.add("交易记录");
            list.add("聊天列表");
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
            viewHolder.textView.setText((String)getItem(i));
            return view;
        }
    }
    class ViewHolder{
        TextView textView;
    }
}
