package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smartschool.smartschooli.R;

import java.util.List;

import bean.Class_Bean;
import utils.MyApplication;

public class Fragment_class_GridView_adapter extends BaseAdapter {

    List<Class_Bean>list;//储存一个周的数据
    public Fragment_class_GridView_adapter(List<Class_Bean> list){
        this.list=list;
    }

    @Override
    public int getCount() {
        return 7*12;
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
            convertView= LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.fragment_class_recycler_item_grid_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.view=convertView.findViewById(R.id.frag_class_dot);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        //得到这个点的具体位置
        int row=position/7+1;
        int coloum=position&7+1;

        //查询该点是否在已获得的课程表中
        boolean flag=selectFromList(row,coloum);
        if(flag){
            viewHolder.view.setBackgroundResource(R.color.yellow);
        }else{
            viewHolder.view.setBackgroundResource(R.color.gray);
        }

        return convertView;
    }


    private boolean selectFromList(int row,int coloum){
        for(Class_Bean bean:list){
            if(bean.getDay()==coloum){
                if(row<=bean.getTo()&&row>=bean.getFrom()){
                    return true;
                }
            }
        }
        return false;
    }

    class ViewHolder{
        View view;
    }
}
