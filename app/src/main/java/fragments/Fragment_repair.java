package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.smartschool.smartschooli.R;
import com.smartschool.smartschooli.RepairDetailActivity;
import com.smartschool.smartschooli.Submit_RepairActivity;
import com.smartschool.smartschooli.Z_MainActivity;

import java.util.ArrayList;
import java.util.List;


import adapter.FragRepair_listview_adapter;
import bean.Repair_Bean;
import listener.LoadingRepairListener;
import rx.observers.TestObserver;
import utils.NetworkLoader;

public class Fragment_repair extends Fragment {
    View view;
    ListView listView;
    List<Repair_Bean> list_ceshi;
    ActionBar actionBar;
    List<Repair_Bean>thing_list;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton floatingActionButton;
    boolean flag=true;//该adapter是否为第一次初始化
    FragRepair_listview_adapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_repair_layout,container,false);
        initViews();


        initEvents();
        initListeners();
        return view;
    }




    public void initViews(){
        listView=(ListView)view.findViewById(R.id.fragment_repair_listView);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.fragment_repair_refresh);
        floatingActionButton=(FloatingActionButton)view.findViewById(R.id.fragemnt_repair_floatButton);
    }


    public void initEvents(){
        NetworkLoader.getInstance().getRepairMessage();
        NetworkLoader.getInstance().setLoadingRepairListener(new LoadingRepairListener() {
            @Override
            public void loadingDown(List<Repair_Bean> list) {

                thing_list=list;
                if(flag){
                    adapter=new FragRepair_listview_adapter(getActivity(),list);
                    listView.setAdapter(adapter);
                    flag=false;

                }else{
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }







    public void initListeners(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Repair_Bean things = thing_list.get(position);
                Intent intent = new Intent(getActivity(),RepairDetailActivity.class);
                intent. putExtra("repairMessage", things);
                intent.putExtra("type","非主管");
                startActivity(intent);
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


        //为悬浮按钮添加点击事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), Submit_RepairActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //为刷新按钮添加点击事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkLoader.getInstance().getRepairMessage();

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
