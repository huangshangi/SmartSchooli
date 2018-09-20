package com.smartschool.smartschooli;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import listener.GetIpListener;
import utils.NetworkLoader;
import utils.Util;

public class LocationActivity extends AppCompatActivity {


    String ip="";

    String address="";

    TextView textView_ip;

    TextView textView_address;

    TextView textView_state;

    TextView textView_network;

    TextView textView_schoolNet;

    Toolbar toolbar;

    SwipeRefreshLayout swipeRefreshLayout;

    String array[]=new String[]{"无网络","wifi","流量"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_layout);
        initViews();
        initEvents();
        initListeners();
    }


    private void initViews(){
        textView_ip=(TextView)findViewById(R.id.ip);
        textView_address=(TextView)findViewById(R.id.address);
        textView_network=(TextView)findViewById(R.id.network_name);
        textView_state=(TextView)findViewById(R.id.state);
        textView_schoolNet=(TextView)findViewById(R.id.school_net);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        NetworkLoader.getInstance().setGetIpListener(new GetIpListener() {
            @Override
            public void getIP(final String ipAddress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[]array=ipAddress.split("!");
                        ip=array[0];
                        address=array[1];
                        textView_ip.setText(array[0]);
                        textView_address.setText(array[1]);
                    }
                });
            }
        });
    }

    private void initEvents(){
        textView_ip.setText(ip);
        textView_address.setText(address);
        textView_state.setText(array[Util.getApnType(this)]);
        NetworkLoader.getInstance().getIp();
        String name=Util.getNetWorkName(this);
        textView_network.setText(name);
        if(name.equals("sdu_net")){
            textView_schoolNet.setText("是");
        }else{
            textView_schoolNet.setText("否");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initListeners(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initEvents();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }






}
