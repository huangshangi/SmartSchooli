package com.smartschool.smartschooli;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import listener.UpdateEvluateListener;
import utils.NetworkLoader;
import view.LoadingProgress;


public class EvluateActivity extends AppCompatActivity {

    int rat1;

    int rat2;

    int rat3;

    String  id;//报修编号

    Toolbar toolbar;

    RatingBar ratingBar1;

    RatingBar ratingBar2;

    RatingBar ratingBar3;

    TextView textView1;//对应评价条

    TextView textView2;

    TextView textView3;

    EditText editText;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evluate_layout);
        id=getIntent().getStringExtra("id");
        initViews();
        initEvents();
        initListeners();
    }

    private void initViews(){
        toolbar=(Toolbar)findViewById(R.id.evluate_layout_toolbar);

        ratingBar1=(RatingBar)findViewById(R.id.ratingResult);
        textView1=(TextView)findViewById(R.id.textView1);
        ratingBar2=(RatingBar)findViewById(R.id.ratingSpeed);
        textView2=(TextView)findViewById(R.id.textView2);
        ratingBar3=(RatingBar)findViewById(R.id.ratingEvaluate);
        textView3=(TextView)findViewById(R.id.textView3);

        editText=(EditText)findViewById(R.id.edittext);
        button=(Button)findViewById(R.id.button);
    }

    private void initEvents(){
        NetworkLoader.getInstance().setUpdateEvluateListener(new UpdateEvluateListener() {
            @Override
            public void upDown(Dialog dialog) {
                LoadingProgress.closeDialog(dialog);
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initListeners(){
        ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    rat1=(int)rating;
                    switch ((int) rating){

                        case 1: textView1.setText("非常差");break;
                        case 2: textView1.setText("差");break;
                        case 3: textView1.setText("一般");break;
                        case 4: textView1.setText("好");break;
                        case 5: textView1.setText("非常好");break;
                    }
                }
            }
        });

        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    rat2=(int)rating;
                    switch ((int) rating){
                        case 1: textView2.setText("非常差");break;
                        case 2: textView2.setText("差");break;
                        case 3: textView2.setText("一般");break;
                        case 4: textView2.setText("好");break;
                        case 5: textView2.setText("非常好");break;
                    }
                }
            }
        });

        ratingBar3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    rat3=(int)rating;
                    switch ((int) rating){
                        case 1: textView3.setText("非常差");break;
                        case 2: textView3.setText("差");break;
                        case 3: textView3.setText("一般");break;
                        case 4: textView3.setText("好");break;
                        case 5: textView3.setText("非常好");break;
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行评价检查
                if(checkEvluate()) {
                    String content = editText.getText().toString();
                    String result = rat1 + "￥" + rat2 + "￥" + rat3 + "￥" + content;
                    Dialog dialog = LoadingProgress.createDialog(EvluateActivity.this, "正在发布评价...");
                    NetworkLoader.getInstance().updateEvluate(id, result, dialog);
                }else{
                    Toast.makeText(EvluateActivity.this,"请先填写评价",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean checkEvluate(){
        if(ratingBar1.getRating()==0||ratingBar2.getRating()==0||ratingBar3.getRating()==0||editText.getText().toString()==null||editText.getText().toString().equals("")){
            return false;
        }
        return true;
    }
}
