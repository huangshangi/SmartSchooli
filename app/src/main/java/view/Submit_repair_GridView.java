package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class Submit_repair_GridView extends GridView {



    public Submit_repair_GridView(Context context){
        super(context);
    }

    public Submit_repair_GridView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
