package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.smartschool.smartschooli.R;

import java.util.List;

import bean.Group_ImageBean;
import utils.ImageLoader;

//图片选择界面适配器
public class ListPopupWindow_Adapter extends BaseAdapter {
    Context context;
    List<Group_ImageBean> list;
    public ListPopupWindow_Adapter(Context context, List<Group_ImageBean> list){

        this.context=context;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.popupview_item_layout,parent,false);
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.popupvieew_listview_imageview);
            viewHolder.textView_name=(TextView)convertView.findViewById(R.id.popupvieew_listview_textname);
            viewHolder.textView_count=(TextView)convertView.findViewById(R.id.popupvieew_listview_textcount);
            viewHolder.imageView_choose=(ImageView)convertView.findViewById(R.id.popupvieew_listview_imageview_choose);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        //为控件添加事件
        Group_ImageBean obj=list.get(position);

        ImageLoader.getInstance().loadImage(obj.getPath(),viewHolder.imageView);
        viewHolder.textView_name.setText(obj.getName());
        viewHolder.textView_count.setText(""+obj.getCount());
        viewHolder.imageView_choose.setImageResource(R.drawable.dir_choose);
        viewHolder.imageView_choose.setVisibility(View.INVISIBLE);
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView_name;
        TextView textView_count;
        ImageView imageView_choose;
    }
}
