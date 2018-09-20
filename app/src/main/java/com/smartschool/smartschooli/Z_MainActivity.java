package com.smartschool.smartschooli;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.FragRepair_listview_adapter;
import bean.NetWork;
import bean.Repair_Bean;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.LoadingRepairListener;
import listener.NetworkGuZhang_Listener;
import utils.ImageLoader;
import utils.NetworkLoader;

public class Z_MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private ListView listView;

    private List<Repair_Bean> thing_list;

    private FragRepair_listview_adapter adapter;

    private ListView navigationView;

    private NavigationView_Adapter adapter_menu;

    private Handler handler;//线程更新

    private View header;

    private TextView textView_name;

    private List<String> person_list;

    private CircleImageView header_image;

    private SwipeRefreshLayout swipeRefreshLayout;

    ImageView imageView;

    private static int position;//记录当前滑倒的位置

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
        header=LayoutInflater.from(this).inflate(R.layout.header_layout,null);
        header_image=(CircleImageView)header.findViewById(R.id.header_layout_image);
        textView_name=(TextView)header.findViewById(R.id.header_layout_name);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        imageView=(ImageView)findViewById(R.id.menu);
    }

    @Override
    protected void onResume() {
        NetworkLoader.getInstance().getRepairMessage();
        super.onResume();
    }



    private void initEvents(){
        NetworkLoader.getInstance().getNetworkGuZhang();
        listView.setSelection(position);
        listView.smoothScrollToPosition(position);
        NetworkLoader.getInstance().getRepairMessage();
        NetworkLoader.getInstance().setLoadingRepairListener(new LoadingRepairListener() {
            @Override
            public void loadingDown(List<Repair_Bean> list) {
                thing_list=list;
                adapter=new FragRepair_listview_adapter(Z_MainActivity.this,list);
                Log.d("Z_MainActivity!!","+lis"+list.size());
                handler.sendEmptyMessage(0x111);
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        navigationView.setAdapter(adapter_menu);
        person_list=NetworkLoader.getInstance().getPersonMessage();


        ImageLoader.getInstance().loadImage(person_list.get(1),header_image);
        textView_name.setText(person_list.get(2));

        navigationView.addHeaderView(header);

        NetworkLoader.getInstance().setNetworkGuZhang_listener(new NetworkGuZhang_Listener() {
            @Override
            public void getMessage(NetWork message) {
                if(message.getGuZhang()){
                    Intent intent=new Intent(Z_MainActivity.this,Z_MainActivity.class);
                    PendingIntent pendingIntent=PendingIntent.getActivity(Z_MainActivity.this,0,intent,0);
                    String id = "my_channel_01";
                    String name="我是渠道名字";
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
                        Toast.makeText(Z_MainActivity.this, mChannel.toString(), Toast.LENGTH_SHORT).show();

                        notificationManager.createNotificationChannel(mChannel);
                        notification = new Notification.Builder(Z_MainActivity.this)
                                .setChannelId(id)
                                .setContentTitle("通知信息")
                                .setContentText("校园网发生故障")
                                .setSmallIcon(R.mipmap.icon).setContentIntent(pendingIntent).build();
                    } else {
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Z_MainActivity.this)
                                .setContentTitle("通知信息")
                                .setContentText("校园网发生故障")
                                .setSmallIcon(R.mipmap.icon).setContentIntent(pendingIntent)
                                .setOngoing(true);

                        notification = notificationBuilder.build();
                    }
                    notificationManager.notify(111123, notification);
                }
            }
        });
    }

    private void initListeners(){

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        //解决listView与swipeFresh冲突问题
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                position=i;
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Repair_Bean things = thing_list.get(i);
                Intent intent = new Intent(Z_MainActivity.this,RepairDetailActivity.class);
                intent.putExtra("repairMessage", things);
                intent.putExtra("type","主管");
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkLoader.getInstance().getRepairMessage();
            }
        });

        navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent=new Intent(Z_MainActivity.this,PhotoSelector.class);
                        intent.putExtra("isOne","Z_MainActivity");
                        startActivityForResult(intent,0x997);
                        break;
                    case 1:
                        Intent intent2=new Intent(Z_MainActivity.this,PersonActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        //点击了聊天列表
                        startActivity(new Intent(Z_MainActivity.this,Chat_List_Activity.class));
                        break;
                    case 3:
                        //故障分析
                        startActivity(new Intent(Z_MainActivity.this,Z_GuZhangActivity.class));
                        break;
                    case 4:
                        //收到的评价
                        startActivity(new Intent(Z_MainActivity.this,Z_EvluatesActivity.class));
                        break;
                    case 5:
                        //退出登录
                        BmobUser.logOut();
                        startActivity(new Intent(Z_MainActivity.this,LoginActivity.class));
                        Z_MainActivity.this.finish();
                        break;

                }
            }
        });

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

    }


    //侧滑栏左侧，数据自己提供
    class NavigationView_Adapter extends BaseAdapter {

        List<String>list;

        public NavigationView_Adapter(){
            list=new ArrayList<>();
            list.add("账号资料");
            list.add("聊天列表");
            list.add("故障分析");
            list.add("我收到的评价");
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
            Drawable drawable=null;
            if(i==0){
                drawable=getResources().getDrawable(R.drawable.personn16);
            } else if(i==1){
                drawable=getResources().getDrawable(R.drawable.chatlist16);
            }else if(i==2){
                drawable=getResources().getDrawable(R.drawable.repair16 );
            }else if(i==3){
                drawable=getResources().getDrawable(R.drawable.evluate116);
            }else if(i==4){
                drawable=getResources().getDrawable(R.drawable.exit16);
            }

            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            viewHolder.textView.setCompoundDrawables(drawable,null,null,null);
            return view;
        }
    }
    class ViewHolder{
        TextView textView;
    }
}
