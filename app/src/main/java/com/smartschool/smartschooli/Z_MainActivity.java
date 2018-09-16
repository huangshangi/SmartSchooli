package com.smartschool.smartschooli;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.FragRepair_listview_adapter;
import bean.Repair_Bean;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import listener.LoadingRepairListener;
import utils.NetworkLoader;

public class Z_MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private ListView listView;

    private List<Repair_Bean> thing_list;

    private FragRepair_listview_adapter adapter;

    private ListView navigationView;

    private NavigationView_Adapter adapter_menu;

    private Handler handler;//线程更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_main_layout);

        initViews();

        initEvents();

        initListeners();

    }


    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.z_mainlayout_drawer);
        thing_list = new ArrayList<>();
        listView=(ListView)findViewById(R.id.list_view);
        adapter_menu=new NavigationView_Adapter();
        navigationView=(ListView)findViewById(R.id.navigationView);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter(adapter);
            }
        };
    }

    private void initEvents(){

        NetworkLoader.getInstance().getRepairMessage();
        NetworkLoader.getInstance().setLoadingRepairListener(new LoadingRepairListener() {
            @Override
            public void loadingDown(List<Repair_Bean> list) {
                thing_list=list;
                adapter=new FragRepair_listview_adapter(Z_MainActivity.this,list);
                Log.d("Z_MainActivity!!","+lis"+list.size());
                handler.sendEmptyMessage(0x111);

            }
        });

        navigationView.setAdapter(adapter_menu);

    }

    private void initListeners(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Repair_Bean things = thing_list.get(i);
                Intent intent = new Intent(Z_MainActivity.this, Z_detailActivity.class);
                intent. putExtra("thing", things);
                startActivity(intent);

            }
        });

        navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }


    //侧滑栏左侧，数据自己提供
    class NavigationView_Adapter extends BaseAdapter {

        List<String>list;

        public NavigationView_Adapter(){
            list=new ArrayList<>();
            list.add("账号资料");
            list.add("聊天列表");
            list.add("故障分析");
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
                view=LayoutInflater.from(Z_MainActivity.this).inflate(R.layout.navigation_item,viewGroup,false);
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
