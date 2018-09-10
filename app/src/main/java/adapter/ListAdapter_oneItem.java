package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;

import utils.NetworkLoader;

//评论适配器
public class ListAdapter_oneItem extends BaseAdapter {
    Context context;
    List<String> list;

    public ListAdapter_oneItem(Context context,List<String> list){

        this.context=context;
        this.list=list;
        if(list==null)
            this.list=new ArrayList<>();

    }

    public void setList(List<String> list) {
        this.list = list;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.commit_item, parent, false);
            viewHolder=new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.textView=(TextView)convertView.findViewById(R.id.shuoshuo_commit_content_item);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        String commit=list.get(position).replace(NetworkLoader.getInstance().getPersonMessage().get(2)+"说:","我说:");
        viewHolder.textView.setText(commit);
        return convertView;
    }
    class ViewHolder{
        TextView textView;
    }
}
