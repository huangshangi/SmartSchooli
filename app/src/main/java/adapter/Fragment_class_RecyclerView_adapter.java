package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Class_Bean;
import utils.MyApplication;

public class Fragment_class_RecyclerView_adapter extends BaseAdapter {

    int currentWeek;//当前所处周数

    int allWeek;//总周数（总周数与map.size不一定相等）

    Map<Integer,List<Class_Bean>> map;

    Fragment_class_GridView_adapter adapter;//GridView的适配器

    public Fragment_class_RecyclerView_adapter(Map<Integer,List<Class_Bean>>map,int currentWeek,int allWeek){
        this.map=map;
        adapter=new Fragment_class_GridView_adapter(map.get(currentWeek));
    }

    @Override
    public int getCount() {
        return allWeek;
    }

    @Override
    public Object getItem(int position) {
        return map.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.fragment_class_recycler_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.gridView=(GridView)convertView.findViewById(R.id.fragment_class_recyclerView_item_grid);
            viewHolder.textView=(TextView)convertView.findViewById(R.id.fragment_class_recyclerView_item_text);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        //准备数据
        List<Class_Bean> list=map.get(position+1);

        //为TextView设置背景
        viewHolder.textView.setText(""+position+1);
        if(position+1==currentWeek){
            viewHolder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.white));
            viewHolder.textView.setBackgroundResource(R.drawable.fragment_class_red_dot);
        }else{
            viewHolder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.gray));
            viewHolder.textView.setBackgroundResource(R.color.white);
        }

        //为gridView设置适配器
        viewHolder.gridView.setAdapter(adapter);
        return convertView;
    }

    class ViewHolder{

        TextView textView;

        GridView gridView;
    }
}
