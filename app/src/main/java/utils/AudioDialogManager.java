package utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartschool.smartschooli.R;

public class AudioDialogManager {

    private Dialog dialog;

    ImageView left_image;

    ImageView right_image;

    TextView textView;

    Context context;
    public AudioDialogManager(Context context){
        this.context=context;
    }

    //初始化对话框
    public void initRecordingDialog(){
        dialog=new Dialog(context, R.style.Theme_AudioDialog);
        View view= LayoutInflater.from(context).inflate(R.layout.audio_dialog,null);
        left_image=(ImageView)view.findViewById(R.id.chat_dialog_leftImage);
        right_image=(ImageView)view.findViewById(R.id.chat_dialog_rightImage);
        textView=(TextView)view.findViewById(R.id.chat_dialog_text);
        dialog.setContentView(view);
        dialog.show();
    }

    //正在录音
    public void showrecodingDialog(){

        if(dialog!=null&&dialog.isShowing()){
            left_image.setVisibility(View.VISIBLE);
            right_image.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            left_image.setImageResource(R.drawable.recorder);
            right_image.setImageResource(R.drawable.v1);
            textView.setText("手指上滑,取消发送");

        }
    }

    //取消发送
    public void showwant_to_cancelDialog(){
        if(dialog!=null&&dialog.isShowing()){

            left_image.setVisibility(View.VISIBLE);
            right_image.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            left_image.setImageResource(R.drawable.cancel);
            textView.setText("手指上滑,取消发送");

        }
    }


    //录音时间太短
    public void showtooShortDialog(){

        if(dialog!=null&&dialog.isShowing()){

            left_image.setVisibility(View.VISIBLE);
            right_image.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            left_image.setImageResource(R.drawable.voice_to_short);
            textView.setText("录音时间太短");

        }
    }

    //改变声音级别
    public void change_imageRight(int level){
        if(dialog!=null&&dialog.isShowing()){

            left_image.setVisibility(View.VISIBLE);
            right_image.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);

            left_image.setImageResource(R.drawable.recorder);
            Log.d("测试音量",level+"");
            int resId=context.getResources().getIdentifier("v"+level,"drawable",context.getPackageName());
            Log.d("音量大小",resId+""+"!!!!!!"+level);
            right_image.setImageResource(resId);
            textView.setText("手指上滑,取消发送");

        }
    }

    public void dismissDialog(){
        if(dialog!=null&&dialog.isShowing()){

            dialog.dismiss();
            dialog=null;
        }
    }
}
