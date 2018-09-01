package adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;

import bean.Class_Bean;
import utils.MyApplication;
import utils.Util;

public class Fragment_class_GridView_adapter extends BaseAdapter {

    List<Class_Bean>list;//储存一个周的数据

    int week;

    public Fragment_class_GridView_adapter(List<Class_Bean> list,int week){

        this.week=week;
        this.list= Util.getInstance().getRealList(list,week);

    }



    @Override
    public int getCount() {
        return 25;
    }

    @Override
    public Object getItem(int position) {
        return list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.fragment_class_recycler_item_grid_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.view_gray=convertView.findViewById(R.id.fragment_class_view_gray);
            viewHolder.view_yellow=convertView.findViewById(R.id.frag_class_view_yellow);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.view_yellow.setVisibility(View.GONE);
        viewHolder.view_gray.setVisibility(View.GONE);
        //得到这个点的具体位置
        int row=position/5+1;
        int coloum=position%5+1;

        Log.d("fangfa","!!!!!!!!!!!!!!"+position);

        //查询该点是否在已获得的课程表中
        boolean flag=selectFromList(row,coloum);
        Log.d("fangfa",flag+"");
        if(flag){
            viewHolder.view_yellow.setVisibility(View.VISIBLE);
        }else{
            viewHolder.view_gray.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    private boolean selectFromList(int row,int coloum){

        for(Class_Bean bean:Util.getInstance().getRealList(list,week)){
            Log.d("fangfa","行列"+row+":"+coloum+":"+bean.getFrom()+":"+bean.getDay());
            if(bean.getDay()==coloum){
                if(row*2<=bean.getTo()&&row*2-1>=bean.getFrom()){
                    return true;
                }
            }
        }
        return false;
    }

    class ViewHolder{
        View view_gray;

        View view_yellow;
    }

}
