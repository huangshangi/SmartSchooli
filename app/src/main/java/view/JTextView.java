package view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;


public class JTextView extends TextView {

    public JTextView(Context context) {
        super(context);
    }

    public JTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
