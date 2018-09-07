package adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;


import com.smartschool.smartschooli.R;
import com.smartschool.smartschooli.Submit_RepairActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.ImageLoader;


public class PhotoSelector_adapter extends BaseAdapter {
    Context context;
    String dir;
    List<String> list;//list中包含的是图片的地址
    boolean isOne;//判断当前图片选择界面被谁调用
    Map<String,CheckBox>hashMap;//String 值代表当前项需要设置的url地址

    public static ArrayList<String>list_selected;//被选择的图片地址


    public PhotoSelector_adapter(Context context, List list,String dir,boolean isOne){
        this.context=context;
        this.list=list;
        this.dir=dir;
        this.isOne=isOne;
        list_selected= Submit_RepairActivity.list_all;
        hashMap=new HashMap<String,CheckBox>();
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

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.photoselector_item, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.photoselector_gridview_item);
            viewHolder.button=(CheckBox) view.findViewById(R.id.photoselector_gridview_button_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //每初始化一个view便将其加入到hashMap
        hashMap.put(list.get(i),viewHolder.button);

        //为imageView设置图片
        DisplayMetrics displayMetrics = viewHolder.imageView.getResources().getDisplayMetrics();
        viewHolder.imageView.setMaxWidth(displayMetrics.widthPixels / 3);
        ImageLoader.getInstance().loadImage((String) list.get(i), viewHolder.imageView);

        //判断已被选择的图片
        viewHolder.button.setChecked(false);
        if(!isOne){
            if(Submit_RepairActivity.list_all.contains(list.get(i))){
                viewHolder.button.setChecked(true);
            }
        }


        addListeners(viewHolder,list.get(i));


        return view;
    }


    public void addListeners(final ViewHolder viewHolder,final String url){
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发表说说界面可以选择多张图片
                if(!isOne) {
                    if(viewHolder.button.isChecked())
                        list_selected.add(url);
                    else if(!viewHolder.button.isChecked()&&list_selected.contains(url))
                        list_selected.remove(url);
                }else{
                    if(list_selected.size()==0){
                        list_selected.add(url);
                    }else{
                        hashMap.get(list_selected.get(0)).setChecked(false);
                        list_selected.clear();
                        list_selected.add(url);
                    }
                }

            }
        });
    }

    class ViewHolder{
        ImageView imageView;
        CheckBox button;
    }
}
