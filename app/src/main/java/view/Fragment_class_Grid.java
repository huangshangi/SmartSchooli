package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class Fragment_class_Grid extends GridView {

    public Fragment_class_Grid(Context context) {
        super(context);
    }

    public Fragment_class_Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(expandSpec, heightMeasureSpec);
    }
}

