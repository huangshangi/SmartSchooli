package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

//课程表界面最左边一栏
public class Fragment_class_listView extends ListView {

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public Fragment_class_listView(Context context){
        super(context);
    }

    public Fragment_class_listView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
