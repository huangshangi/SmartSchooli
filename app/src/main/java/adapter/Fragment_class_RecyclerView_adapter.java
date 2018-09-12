package adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Class_Bean;
import listener.Fragment_class_Change_Listener;
import utils.MyApplication;

public class Fragment_class_RecyclerView_adapter extends RecyclerView.Adapter{

    TextView textView_selected;//当前被选择的TextView

    Map<String,TextView>map=new HashMap<>();;//一个专门储存TextView的list
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        TextView textView;

        GridView gridView;
        public ViewHolder(View view){
            super(view);
            this.view=view;
            gridView=(GridView)view.findViewById(R.id.fragment_class_recyclerView_item_grid);
            textView=(TextView)view.findViewById(R.id.fragment_class_recyclerView_item_text);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.fragment_class_recycler_item,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);




        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder=(ViewHolder)holder;
        viewHolder.textView.setText(""+(position+1));
        map.put(position+1+"",viewHolder.textView);
        if(position+1==week){

            viewHolder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.white));
            viewHolder.textView.setBackgroundResource(R.drawable.fragment_class_red_dot);
            textView_selected=viewHolder.textView;
        }else{
            viewHolder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.lightgrey));
            viewHolder.textView.setBackgroundResource(R.drawable.fragment_class_gray_dot);
        }


        //为gridView设置适配器
        viewHolder.gridView.setAdapter(new Fragment_class_GridView_adapter(list,position+1));

        //为recylerView设置点击事件
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=viewHolder.getAdapterPosition();

                week=position+1;
                textView_selected.setTextColor(MyApplication.getContext().getResources().getColor(R.color.gray));
                textView_selected.setBackgroundResource(R.drawable.fragment_class_gray_dot);

                textView_selected=viewHolder.textView;
                viewHolder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.white));
                viewHolder.textView.setBackgroundResource(R.drawable.fragment_class_red_dot);

                //回调改变课程信息
                if(listener!=null){
                    listener.changeClass(week);
                }

            }
        });
    }

    public void PopupWindowSelected(int position){

        textView_selected.setTextColor(MyApplication.getContext().getResources().getColor(R.color.gray));
        textView_selected.setBackgroundResource(R.color.white);
        textView_selected=map.get(position+"");
        textView_selected.setTextColor(MyApplication.getContext().getResources().getColor(R.color.white));
        textView_selected.setBackgroundResource(R.drawable.fragment_class_red_dot);
    }

    int week;//当前所处周数

    int allWeek;//总周数（总周数与map.size不一定相等）

    List<Class_Bean> list;

    Fragment_class_GridView_adapter adapter;//GridView的适配器

    Fragment_class_Change_Listener listener;

    public Fragment_class_RecyclerView_adapter(List<Class_Bean>list,int week,int allWeek){
        this.list=list;
        Log.d("fangfa",list.size()+"!!!!!!!!!@@@@@@@@@"+week);
        this.week=week;
        this.allWeek=allWeek;
    }


    public void setMessage(List<Class_Bean>list,int week,int allWeek){
        this.list=list;
        Log.d("fangfa",list.size()+"!!!!!!!!!@@@@@@@@@"+week);
        this.week=week;
        this.allWeek=allWeek;
    }

    public void setListener(Fragment_class_Change_Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return allWeek;
    }
}
