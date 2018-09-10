package view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.smartschool.smartschooli.ChatActivity;
import com.smartschool.smartschooli.R;

import utils.NetworkLoader;

/*
    一个实现下拉刷新的listView
 */
public class Chat_ListView extends ListView implements AbsListView.OnScrollListener{

    View header;//隐藏条

    int firstItem;//当前listView可见的第一个条目

    int scrollState;//当前手指状态（下拉，释放）

    int firstY;//手指第一次按下的位置

    int state;
    //当前listView所处状态的划分

    static int header_height;//隐藏条的高度

    final int STATE_NONE=1;//正常滑动状态

    final int STATE_PREPARE_REFRESH=2;//将要进行下拉刷新

    final int STATE_REFERSH=3;//将下拉刷新按钮下拉状态

    final int STATE_PREPARE_RELEASE=4;//刷新完成释放状态


    public Chat_ListView(Context context) {
        super(context);
        initView(context);
    }

    public Chat_ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Chat_ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //将header加入到listView
    public void initView(Context context){
        header= LayoutInflater.from(context).inflate(R.layout.chat_listview_header,null);

        //测量header高度
        measureView(header);

        int height=header_height=header.getMeasuredHeight();

        header.setPadding(header.getPaddingLeft(),-height,header.getPaddingRight(),header.getPaddingBottom());
        header.invalidate();

        this.addHeaderView(header);
        this.setOnScrollListener(this);
    }


    //测试控件大小
    private void measureView(View view){
        ViewGroup.LayoutParams params=view.getLayoutParams();
        if(params==null){
            params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width=ViewGroup.getChildMeasureSpec(0,0,params.width);
        int height=params.height;
        if(height>0){
            height=MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        }else{
            height=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width,height);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int height=header_height;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(firstItem==0){
                    //将要进行下拉刷新
                    firstY=(int) ev.getY();
                    state=STATE_PREPARE_REFRESH;
                    Log.d("state!!!",state+"");
                }else{
                    state=STATE_NONE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(state==STATE_PREPARE_REFRESH){
                    int tempY=(int)ev.getY();
                    int space=tempY-firstY;//如果该数为正值，则处于下拉刷新状态

                    Log.d("state!!!","下拉距离"+space+"!"+height);
                    if(space>0){
                        //将界面下移
                        header.setPadding(header.getPaddingLeft(),space-height,header.getPaddingRight(),header.getPaddingBottom());
                        header.invalidate();
                        if(space>height){
                            state=STATE_PREPARE_RELEASE;

                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(state==STATE_PREPARE_RELEASE){
                    //下拉刷新,额外获取15条数据

                    NetworkLoader.getInstance().getMessage(ChatActivity.own_id,ChatActivity.target_id,1);
                    Log.d("&&&&&&&&&&&&&&","执行了该方法");

                    state=STATE_NONE;
                }
                break;

        }

        return super.onTouchEvent(ev);
    }
    public void hide_refresh(){
        header.setPadding(header.getPaddingLeft(),-header_height,header.getPaddingRight(),header.getPaddingBottom());
        header.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state) {
        scrollState=state;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstItem, int visibleCount, int totalItemCount) {
        this.firstItem=firstItem;
        Log.d("state!!!","the firstItem is"+firstItem);
    }


}
