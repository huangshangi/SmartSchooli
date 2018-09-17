package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeoutException;

import bean.Repair_Bean;
import listener.Z_getEvluateListener;
import utils.NetworkLoader;

public class Z_EvluatesActivity extends AppCompatActivity {


    ListView listView;

    List_Adapter list_adapter;

    NetworkLoader networkLoader;

    TextView textView_visible;

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_evluatelayout);
        listView=(ListView)findViewById(R.id.listView);
        textView_visible=(TextView)findViewById(R.id.visibleText);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                listView.setAdapter(list_adapter);
            }
        };
        networkLoader=NetworkLoader.getInstance();
        networkLoader.Z_getEvluates(networkLoader.getPersonMessage().get(0));
        networkLoader.setZ_getEvluateListener(new Z_getEvluateListener() {
            @Override
            public void loadingDown(List<Repair_Bean> list) {
                if(list==null||list.size()==0){
                    textView_visible.setVisibility(View.VISIBLE);
                }else {
                    textView_visible.setVisibility(View.GONE);
                    list_adapter = new List_Adapter(list);
                    handler.sendEmptyMessage(0x1);
                }
            }
        });

        initListeners();
    }


    private void initListeners(){

    }

    class List_Adapter extends BaseAdapter{
        List<Repair_Bean>list;
        public List_Adapter(List<Repair_Bean>list){
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
                convertView= LayoutInflater.from(Z_EvluatesActivity.this).inflate(R.layout.z_evluate,parent,false);
                viewHolder.linearLayout=(LinearLayout)convertView.findViewById(R.id.linearlayout);
                viewHolder.textView_name=(TextView)convertView.findViewById(R.id.name);
                viewHolder.textView_content=(TextView)convertView.findViewById(R.id.evaluate);
                viewHolder.imageView=(ImageView)convertView.findViewById(R.id.imageView);
                convertView.setTag(convertView);
            }else {
                viewHolder=(ViewHolder)convertView.getTag();
            }
            final Repair_Bean bean=list.get(position);
            viewHolder.textView_content.setText(bean.getEvluate_content().substring(bean.getEvluate_content().lastIndexOf("￥")+1));
            viewHolder.textView_name.setText(bean.getRepairer_name());
            //得到评价星级
            String result=bean.getEvluate_content();
            int evluate=0;
            if(result==null||result.equals("")){

            }else{
                int index=result.lastIndexOf("￥");
                String[]fenshus=result.substring(0,index).split("￥");
                int a=Integer.valueOf(fenshus[0]);
                int b=Integer.valueOf(fenshus[1]);
                int c=Integer.valueOf(fenshus[2]);
                evluate=(a+b+c);
            }


            if(evluate>=10){
                viewHolder.imageView.setImageResource(R.drawable.good);
            }else if(evluate>=5){
                viewHolder.imageView.setImageResource(R.drawable.normal);
            }else{
                viewHolder.imageView.setImageResource(R.drawable.bad);
            }

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id=bean.getRepair_bianhao();
                    Intent intent=new Intent(Z_EvluatesActivity.this,ReadEvluateActivity.class);
                    intent.putExtra("bean",bean);
                    intent.putExtra("flag",false);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    class ViewHolder{
        LinearLayout linearLayout;
        TextView textView_name;
        TextView textView_content;
        ImageView imageView;
    }
}
