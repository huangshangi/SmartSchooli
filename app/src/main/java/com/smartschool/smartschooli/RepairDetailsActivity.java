package com.smartschool.smartschooli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import bean.Repair_Bean;
import listener.LoadingOwnRepairListener;
import utils.NetworkLoader;

public class RepairDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair_details_layout);

        initViews();
        initEvents();
    }


    private void initViews(){
        toolbar=(Toolbar)findViewById(R.id.repair_details_layout_toolbar);

        listView=(ListView)findViewById(R.id.repair_details_layout_listView);

    }

    private void initEvents(){

        NetworkLoader.getInstance().getOwnRepairMessage();
        //获取提交数据

        NetworkLoader.getInstance().setLoadingOwnRepairListener(new LoadingOwnRepairListener() {
            @Override
            public void loadingDown(List<Repair_Bean> list) {
                listView.setAdapter(new Listadapter(list));
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }

            final Repair_Bean bean=list.get(position);
            viewHolder.textView_bianhao.setText(position+1+"  "+bean.getRepair_bianhao());
            viewHolder.textView_evaluate.setText(bean.getEvluate_status());
            viewHolder.textView_address.setText(bean.getRepair_adress());
            viewHolder.textView_handle.setText(bean.getRepair_status());
            viewHolder.textView_machine.setText(bean.getRepair_machine());
            viewHolder.textView_type.setText(bean.getRepair_type());
            //为点击详情设立点击事件
            viewHolder.textView_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(RepairDetailsActivity.this,RepairDetailActivity.class);
                    intent.putExtra("repairMessage",bean);
                    startActivity(intent);

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

        }
    }

}
