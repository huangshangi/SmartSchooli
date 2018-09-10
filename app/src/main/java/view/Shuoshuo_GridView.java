package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class Shuoshuo_GridView extends GridView {

    public Shuoshuo_GridView(Context context){
        super(context);
    }

    public Shuoshuo_GridView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
