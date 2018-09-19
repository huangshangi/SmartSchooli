package view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import listener.Wheel_listener;
import utils.Util;

public class WheelView extends ScrollView {

    final static int UP=1;

    final static int DOWN=-1;

    final static int NORMAL=0;

    int STATUS=0;//记录当前手指状态

    private Context context;

    private int itemHeight;//每个条目的高度

    private int selectIndex=1;//被选择的序号

    private int[]array;//被选择的项上下边界

    private int oldY;

    private int viewWidth;//屏幕宽度

    private Paint paint;

    private LinearLayout linearLayout;//WheelView子项

    private List<String> list;//盛放数据

    private Runnable runnable;//手指抬起时执行该任务

    private int offset;//偏移量

    private int PAGE_COUNT=5;//每页显示的item个数

    private Wheel_listener wheel_listener;

    public void setWheel_listener(Wheel_listener wheel_listener) {
        this.wheel_listener = wheel_listener;
    }

    private void setOffset(int offset){
        this.offset=offset;

    }

    public void setPAGE_COUNT(int PAGE_COUNT) {
        this.PAGE_COUNT = PAGE_COUNT;
        setOffset((PAGE_COUNT-1)/2);
    }

    public void setList(List<String>list){
        this.list=list;
        for(int i=0;i<offset;i++){
            this.list.add(0,"");
            this.list.add("");
        }
        Log.d("Tagtag",list.size()+"");
        for(String str:list)
            linearLayout.addView(createTextView(str));
    }

    public WheelView(Context context) {

        super(context);
        this.context=context;
        init();
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public void setSelection(final int position){
        post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0,itemHeight*(position));
            }
        });
    }



    private void init(){
            linearLayout=new LinearLayout(context);

            linearLayout.setOrientation(LinearLayout.VERTICAL);

            setVerticalScrollBarEnabled(false);



            this.addView(linearLayout);

            runnable=new Runnable() {
                @Override
                public void run() {

                    final int newY=getScrollY();

                    if(newY==oldY){
                        final int jiange=newY/itemHeight;

                        final int shengyu=newY%itemHeight;

                        if(shengyu==0){
                            selectIndex=jiange+offset;

                        }else if(shengyu>itemHeight/2){
                            selectIndex=jiange+1+offset;

                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {

                                    WheelView.this.smoothScrollTo(0,newY+itemHeight-shengyu);
                                }
                            });
                        }else{
                            selectIndex=jiange+offset;
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {

                                    WheelView.this.smoothScrollTo(0,newY-shengyu);
                                }
                            });
                        }
                        if(wheel_listener!=null){
                            wheel_listener.onSelected(selectIndex);

                            Log.d("Tagtag","WheelView之心"+selectIndex);
                        }
                    }else{
                        oldY=getScrollY();
                        postDelayed(runnable,50);
                    }
                }
            };

    }

    private int[] obtainArray(){
        if(array==null) {
            array = new int[2];
            array[0] = (offset) * itemHeight;

            array[1] = (offset + 1) * itemHeight;
        }
        return array;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth=w;
        setBackgroundDrawable(null);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {

        Log.d("Tagtag","方法执行");
        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();

        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.parseColor("#ff0000"));
            paint.setStrokeWidth(Util.getInstance().dp2px(1));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(0, obtainArray()[0], viewWidth, obtainArray()[0], paint);
                canvas.drawLine(0, obtainArray()[1], viewWidth, obtainArray()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };


        super.setBackgroundDrawable(background);

    }

    private void startScroll(){
        oldY=getScrollY();
        postDelayed(runnable,50);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(t>oldt){
            //下滑
            STATUS=DOWN;
        }else if(t<oldt){
            //上滑
            STATUS=UP;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP){
            startScroll();
        }

        return super.onTouchEvent(ev);
    }

    private TextView createTextView(String str){
        TextView textView=new TextView(context);

        textView.setText(str);

        textView.setGravity(Gravity.CENTER);

        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textView.setSingleLine(true);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);

        int padding= Util.getInstance().dp2px(10);

        textView.setPadding(padding,padding,padding,padding);

        if(itemHeight==0){
            itemHeight=getViewMeasuredHeight(textView);

            post(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight*PAGE_COUNT));

                    WheelView.this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight*PAGE_COUNT));
                }
            });
        }
        return textView;
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }


}
