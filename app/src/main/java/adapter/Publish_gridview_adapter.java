package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;

import utils.ImageLoader;
import utils.NetworkLoader;
import utils.Util;

public class Publish_gridview_adapter extends BaseAdapter {
    Context context;
    List list;//list中包含的是图片的地址

    public Publish_gridview_adapter(Context context, List list){
        this.context=context;
        this.list=list;
        if(list==null)
            this.list=new ArrayList();
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

       final ViewHolder viewHolder = new ViewHolder();


            view = LayoutInflater.from(context).inflate(R.layout.publish_item_layout, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.publish_imageview);
            view.setTag(viewHolder);


        if(list.get(i) instanceof String) {
            DisplayMetrics displayMetrics = viewHolder.imageView.getResources().getDisplayMetrics();
            viewHolder.imageView.setMaxWidth(displayMetrics.widthPixels / 3);
            viewHolder.imageView.setMaxHeight(100);

                ImageLoader.getInstance().loadImage((String) list.get(i), viewHolder.imageView);

        }else if(list.get(i) instanceof Integer){
            viewHolder.imageView.setImageResource((int)list.get(0));
        }else if(list.get(i) instanceof Bitmap){ viewHolder.imageView.setImageBitmap((Bitmap) list.get(i));
        }


        return view;
    }

    class ViewHolder{
        ImageView imageView;
    }
}
