package view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.smartschool.smartschooli.R;

import utils.MyApplication;

//用来创建一个Dialog
public class LoadingProgress{

    Context context;
    String msg;



    public static Dialog createDialog(Context context,String msg){
        View view= LayoutInflater.from(context).inflate(R.layout.progress_loading_layout,null);

        LinearLayout layout=(LinearLayout)view.findViewById(R.id.dialog_loading_view);
        TextView textView=(TextView)view.findViewById(R.id.loading_TextView);
        textView.setText(msg);

        Dialog dialog=new Dialog(context,R.style.MyDialogStyle);


        dialog.setCancelable(true);
        dialog. setCanceledOnTouchOutside(false);
        dialog.setContentView(layout,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }



    public static void closeDialog(Dialog dialog){
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
