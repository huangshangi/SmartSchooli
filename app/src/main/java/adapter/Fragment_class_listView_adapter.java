package adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;

import view.Fragment_class_listView;

public class Fragment_class_listView_adapter extends BaseAdapter {

    List<String> list;//内容储存节次

    Context context;

    public Fragment_class_listView_adapter(Context context){
        this.context=context;
        initDatas();
    }

    //代表1-12节课
    private void initDatas(){
        list=new ArrayList<>();
        for(int i=1;i<13;i++){
            list.add(String.valueOf(i));
        }
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
        TextView textView=new TextView(context);

        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.fragment_class_tv_bg);
        textView.setText(list.get(i));

        int width=getDismension()/8;
        textView.setWidth(width);
        textView.setHeight(width);

        return textView;
    }

    //获取屏幕宽度
    private int getDismension(){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
