package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bean.Repair_Bean;
import listener.LoadingOwnRepairListener;
import utils.NetworkLoader;

public class RepairDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    ListView listView;

    TextView textView_visible;

    SwipeRefreshLayout swipeRefreshLayout;

    Handler handler;

    int xuhao;//被点击的序号

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
        setContentView(R.layout.repair_details_layout);
        initViews();
        initEvents();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkLoader.getInstance().getOwnRepairMessage();

    }

    private void initListeners(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initEvents();;
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //解决listView与swipeFresh冲突问题
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }

    private void initViews(){
        toolbar=(Toolbar)findViewById(R.id.repair_details_layout_toolbar);

        listView=(ListView)findViewById(R.id.repair_details_layout_listView);

        textView_visible=(TextView)findViewById(R.id.visible);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<Repair_Bean>list=(List<Repair_Bean>) msg.obj;
                listView.setAdapter(new Listadapter(list));
            }
        };
    }

    private void initEvents(){

        NetworkLoader.getInstance().getOwnRepairMessage();
        //获取提交数据

        NetworkLoader.getInstance().setLoadingOwnRepairListener(new LoadingOwnRepairListener() {
            @Override
            public void loadingDown(List<Repair_Bean> list) {
                if(list==null||list.size()==0){
                    textView_visible.setVisibility(View.VISIBLE);
                }else {
                    textView_visible.setVisibility(View.GONE);
                    Message message = new Message();
                    message.obj = list;
                    handler.sendMessage(message);
                }
            }
        });


    }


    class Listadapter extends BaseAdapter{

        List<Repair_Bean>list;

        public Listadapter(List<Repair_Bean> list){
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder=new ViewHolder();
                convertView= LayoutInflater.from(RepairDetailsActivity.this).inflate(R.layout.repair_details_layout_item,parent,false);

                viewHolder.textView_bianhao=(TextView)convertView.findViewById(R.id.repair_details_text);
                viewHolder.textView_evaluate=(TextView)convertView.findViewById(R.id.repair_details_evaluate);
                viewHolder.textView_address=(TextView)convertView.findViewById(R.id.repair_details_address);
                viewHolder.textView_details=(TextView)convertView.findViewById(R.id.repair_details_details);
                viewHolder.textView_handle=(TextView)convertView.findViewById(R.id.repair_details_handle);
                viewHolder.textView_machine=(TextView)convertView.findViewById(R.id.repair_details_machine);
                viewHolder.textView_type=(TextView)convertView.findViewById(R.id.repair_details_type);
                viewHolder.textView_time=(TextView)convertView.findViewById(R.id.repair_details_time);
                viewHolder.linearLayout=(LinearLayout)convertView .findViewById(R.id.repair_details_linearlayout);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }

            final Repair_Bean bean=list.get(position);
            viewHolder.textView_bianhao.setText(position+1+"  "+bean.getRepair_bianhao());
            viewHolder.textView_evaluate.setText(bean.getEvluate_status());
            viewHolder.textView_address.setText(bean.getRepair_adress());
            if(bean.getHandle()){
                viewHolder.textView_handle.setText("已处理");
            }else{
                viewHolder.textView_handle.setText("未处理");
            }

            viewHolder.textView_machine.setText(bean.getRepair_machine());
            viewHolder.textView_type.setText(bean.getRepair_type());
            viewHolder.textView_time.setText(bean.getCreatedAt().substring(5,10));
            //为点击详情设立点击事件
            viewHolder.textView_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xuhao=position;
                    Intent intent=new Intent(RepairDetailsActivity.this,RepairDetailActivity.class);
                    intent.putExtra("repairMessage",bean);//传入报修编号
                    intent.putExtra("type",NetworkLoader.getInstance().getPersonMessage().get(3));
                    startActivity(intent);

                }
            });
            //点击评价控件
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xuhao=position;
                    //已评价
                    if(bean.getEvluate_status().equals("是")){
                        Intent intent=new Intent(RepairDetailsActivity.this,ReadEvluateActivity.class);
                        intent.putExtra("bean",bean);
                        intent.putExtra("flag",true);
                        startActivity(intent);
                    }else{
                        if(bean.getHandle()) {
                            Intent intent = new Intent(RepairDetailsActivity.this, EvluateActivity.class);
                            intent.putExtra("id", bean.getRepair_bianhao());
                            startActivity(intent);
                        }else{
                            Toast.makeText(RepairDetailsActivity.this,"该故障还未被处理",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            return convertView;
        }

        class ViewHolder{

            TextView textView_bianhao;

            TextView textView_details;

            TextView textView_machine;

            TextView textView_type;

            TextView textView_address;


            TextView textView_time;

            TextView textView_handle;

            TextView textView_evaluate;

            LinearLayout linearLayout;//评价控件

        }
    }

}
