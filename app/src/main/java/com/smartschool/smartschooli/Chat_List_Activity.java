package com.smartschool.smartschooli;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;



import java.util.List;

import adapter.FragChat_list_Adapter;

import bean.UpdateMessage_Bean;
import listener.UpdateMessageListener;
import utils.NetworkLoader;
import utils.VibrateAndBeeManager;

//消息记录类
public class Chat_List_Activity extends AppCompatActivity {

    FragChat_list_Adapter adapter;

    Handler handler;

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
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        listView.setAdapter(adapter);
                        break;
                    case 1:
                        VibrateAndBeeManager.Bee();
                        break;
                }
            }
        };
    }


    public void initEvents(){
        NetworkLoader.getInstance().setUpdateMessageListener(new UpdateMessageListener() {
            @Override
            public void getUpdateMessage(List<UpdateMessage_Bean> list) {
                if(flag){
                    adapter=new FragChat_list_Adapter(list,Chat_List_Activity.this);
                    handler.sendEmptyMessage(0);
                    flag=false;
                }else{
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void getRedDotMessage() {
                handler.sendEmptyMessage(1);
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
                NetworkLoader.getInstance().messageHasRead(target_id,NetworkLoader.getInstance().getPersonMessage().get(0));
                Intent intent=new Intent(Chat_List_Activity.this,ChatActivity.class);
                intent.putExtra("target_id",target_id);
                intent.putExtra("own_id", NetworkLoader.getInstance().getPersonMessage().get(0));
                intent.putExtra("target_name",target_name);
                startActivity(intent);
            }
        });
    }

}
