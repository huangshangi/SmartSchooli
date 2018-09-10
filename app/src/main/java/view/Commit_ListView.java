package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class Commit_ListView extends ListView {
    public Commit_ListView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
