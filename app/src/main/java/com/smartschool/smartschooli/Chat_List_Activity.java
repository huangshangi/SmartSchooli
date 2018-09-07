package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;



import java.util.List;

import adapter.FragChat_list_Adapter;

import bean.Person;
import bean.UpdateMessage_Bean;
import listener.UpdateMessageListener;
import utils.AudioManager;
import utils.NetworkLoader;
import utils.VibrateAndBeeManager;

//消息记录类
public class Chat_List_Activity extends AppCompatActivity {

    FragChat_list_Adapter adapter;

    ListView listView;
    boolean flag=true;//判断适配器是否为第一次初始化

    @Override
    protected void onResume() {
        super.onResume();
        NetworkLoader.getInstance().getUpdateMessage();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat_list);
        initViews();
        initEvents();
        initListeners();
    }

    public void initViews(){
        listView=(ListView)findViewById(R.id.fragment_chat_listView);
        NetworkLoader.getInstance().getUpdateMessage();
    }


    public void initEvents(){
        NetworkLoader.getInstance().setUpdateMessageListener(new UpdateMessageListener() {
            @Override
            public void getUpdateMessage(List<UpdateMessage_Bean> list) {
                if(flag){
                    adapter=new FragChat_list_Adapter(list,Chat_List_Activity.this);
                    listView.setAdapter(adapter);
                    flag=false;
                }else{
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void getRedDotMessage() {
                VibrateAndBeeManager.Bee();
            }
        });


    }

    public void initListeners(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String target_id=(String)view.findViewById(R.id.chat_list_item_name).getTag();
                String target_name=((TextView)view.findViewById(R.id.chat_list_item_name)).getText().toString();

                //该信息已读
                NetworkLoader.getInstance().messageHasRead(target_id,Person.getId());
                Intent intent=new Intent(Chat_List_Activity.this,null);
                intent.putExtra("target_id",target_id);
                intent.putExtra("own_id", Person.getId());
                intent.putExtra("target_name",target_name);
                startActivity(intent);
            }
        });
    }

}
