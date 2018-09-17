package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

import java.util.List;

import bean.Repair_Bean;

//维修记录的适配器
public class FragRepair_listview_adapter extends BaseAdapter {
    Context context;
    List<Repair_Bean> list;

    public FragRepair_listview_adapter(Context context,List<Repair_Bean> list){
        this.context=context;
        this.list=list;
    }

    public void setList(List<Repair_Bean>list){
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Repair_Bean obj=list.get(i);
        ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.fragment_repair_layout_item,viewGroup,false);
            viewHolder=new ViewHolder();
            viewHolder.textView_name=(TextView)view.findViewById(R.id.fragment_repair_item_name);
            viewHolder.textView_machine_name=(TextView)view.findViewById(R.id.fragment_repair_item_machine);
            viewHolder.textView_time=(TextView)view.findViewById(R.id.fragment_repair_item_time);
            viewHolder.textView_status=(TextView)view.findViewById(R.id.fragment_repair_item_status);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.textView_name.setText(obj.getRepairer_name());
        viewHolder.textView_machine_name.setText(obj.getRepair_machine());
        viewHolder.textView_time.setText(obj.getCreatedAt().substring(0,10));
        if(obj.getHandle()){
            viewHolder.textView_status.setText("已处理");
        }else{
            viewHolder.textView_status.setText("未处理");
        }

        String elses=obj.getRepairer_id()+"$"+obj.getRepair_type()+"$"+obj.getRepair_type()+"$"+obj.getRepair_adress()+"$"+obj.getRepair_content()+"$"+
                obj.getRepair_urls()+"$"+obj.getRepair_bianhao();
        viewHolder.textView_name.setTag(elses);

        return view;
    }


    class ViewHolder{
        TextView textView_name;
        TextView textView_machine_name;
        TextView textView_status;
        TextView textView_time;
    }
}
