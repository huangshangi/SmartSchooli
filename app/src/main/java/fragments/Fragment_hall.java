package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.smartschool.smartschooli.PublishActivity;
import com.smartschool.smartschooli.R;


import adapter.PhotoSelector_adapter;
import adapter.RecyclerView_Adapter;
import bean.Shuoshuo_Bean;
import listener.LoadingListener;
import utils.MyApplication;
import utils.NetworkLoader;


import java.util.ArrayList;
import java.util.List;

public class Fragment_hall extends Fragment {
    Toolbar toolbar;
    View view;
    NetworkLoader networkLoader;
    RecyclerView recyclerView;
    RecyclerView_Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean flag_init=true;//用来标记是否是第一次加载
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_hall_layout,container,false);
        initViews();
        handleFragActionBar();
        addListeners();
        initEvents();

        return view;
    }

    public void handleFragActionBar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
    }


    public void initViews(){

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.fragment_hall_swipeRefreshLayout);
        recyclerView=(RecyclerView)view.findViewById(R.id.fragment_hall_recyclerView);
        toolbar=(Toolbar)view.findViewById(R.id.fragment_hall_toolbar);
        networkLoader=NetworkLoader.getInstance();

    }


    public void initEvents(){
        networkLoader.setLoadingListener(new LoadingListener() {
            @Override
            public void loadingDown(List<Shuoshuo_Bean> list) {
                onFresh(list);
            }
        });

        networkLoader.getShuoshuo();
    }

    public void addListeners(){


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkLoader.getShuoshuo();
                Toast.makeText(getContext(),"刷新完成",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //下拉刷新时调用该方法
    public void onFresh(List<Shuoshuo_Bean>list){

        if(flag_init) {
            adapter = new RecyclerView_Adapter(MyApplication.getContext(), list);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
            flag_init=false;
        }else{
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_shuoshuo:
                Intent intent=new Intent(getActivity(), PublishActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_hall_menu,menu);
    }
}
