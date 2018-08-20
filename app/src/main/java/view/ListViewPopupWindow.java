package view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.smartschool.smartschooli.R;

import adapter.ListPopupWindow_Adapter;
import bean.Group_ImageBean;
import listener.PopupListener;


import java.util.List;

public class ListViewPopupWindow extends PopupWindow {
    int width;
    int height;
    ListView listView;
    View view;
    Context context;
    List<Group_ImageBean> list;
    PopupListener listener;
    public ListViewPopupWindow(Context context,List<Group_ImageBean> list){
        this.context=context;
        this.list=list;
        measure();
        view= LayoutInflater.from(context).inflate(R.layout.popupview_layout,null);
        listView=(ListView)view.findViewById(R.id.popupview_listview);
        setContentView(view);
        setAnimationStyle(R.style.PopupAnimation);
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        if(list==null){
            Log.d("!!!!!!!!!!","为空");

        }else {
            Log.d("!!!!!!!!!!","bu为空");
            //设置适配器
            ListPopupWindow_Adapter adapter = new ListPopupWindow_Adapter(context, list);

            listView.setAdapter(adapter);
            addListener();
        }
    }


    public void addListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listener!=null){
                    listener.onSelect(i);
                }
            }
        });
    }

    public void measure(){
        DisplayMetrics displayMetrics;
        displayMetrics=context.getResources().getDisplayMetrics();
        width=displayMetrics.widthPixels;
        height=(int)(displayMetrics.heightPixels*0.7f);
    }
    public void setPopupListener(PopupListener listener){
        this.listener=listener;
    }
}
