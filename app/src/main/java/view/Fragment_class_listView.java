package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

//课程表界面最左边一栏
public class Fragment_class_listView extends ListView {

    public Fragment_class_listView(Context context){
        super(context);
    }

    public Fragment_class_listView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public Fragment_class_listView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
