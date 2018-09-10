package com.smartschool.smartschooli;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.PhotoSelector_adapter;
import adapter.Publish_gridview_adapter;
import listener.Publish_Shuoshuo_Listener;
import utils.MyApplication;
import utils.NetworkLoader;
import view.LoadingProgress;

/*
    发表说说界面
 */
public class PublishActivity extends AppCompatActivity {

    File file;//储存拍照照片的路径
    GridView gridView;
    EditText editText;
    PopupWindow popupWindow;
    NetworkLoader networkLoader;
    Publish_gridview_adapter adapter;
    View view2;//popupWindow的布局文件
    public static ArrayList list_all;//list内部储存的是照片的决定路径

    final int FROM_ALUBM=1;
    final   int TAKE_PHOTO=2;
    final int ASK_PREMISSION=3;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_layout);
        initViews();
        initActionBar();
        initListeners();
        initEvents();
    }

    public void initEvents(){
        networkLoader.setPublish_shuoshuo_listener(new Publish_Shuoshuo_Listener() {
            @Override
            public void Publish_ShuoshuoDown() {
                finish();
            }
        });

        if(list_all.size()==0)
            list_all.add(R.drawable.add);
    }

    public void initListeners(){
        //启动时不显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    //用户点击add按钮，弹出popwindow，让用户选择从相册中选取还是拍摄

                    if(popupWindow.isShowing()){

                    }else {
                        lightOff();
                        showPopWindow();
                    }


                }else{
                    //图片查看器（待实现）

                }
            }
        });
    }

    //进入photoselector界面，并等待返回用户选择的图片
    public void fromAlubm(){

        Intent intent=new Intent(PublishActivity.this,PhotoSelector.class);
        intent.putExtra("isOne","PublishActivity");
        startActivityForResult(intent,FROM_ALUBM);
    }

    //popWindow的弹出方法
    public void showPopWindow(){
        // 隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);



        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        TextView textView_takePhoto=(TextView)view2.findViewById(R.id.popwindow_takephoto);
        TextView textView_alubm=(TextView)view2.findViewById(R.id.popwindow_alubm);
        TextView textView_cancel=(TextView)view2.findViewById(R.id.popwindow_cancel);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });

        textView_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //takephoto
                checkPremission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                takePhoto();

                popupWindow.dismiss();
                lightOn();
            }
        });


        textView_alubm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从相册中选取
                fromAlubm();
                popupWindow.dismiss();
                lightOn();
            }
        });

        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                lightOn();
            }
        });

        View conView=LayoutInflater.from(this).inflate(R.layout.publish_layout,null);
        popupWindow.showAtLocation(conView, Gravity.BOTTOM,0,0);
    }





    public boolean checkPremission(String permission){
        if(ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},ASK_PREMISSION);
        }else{
            return true;
        }
        return false;
    }

    //权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case ASK_PREMISSION:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"授权成功",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(this,"授权失败",Toast.LENGTH_SHORT).show();
                }
        }
    }



    //返回用户选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case FROM_ALUBM:
                if(resultCode==RESULT_OK){

                    List list=data.getStringArrayListExtra("list");
                    for(int i=0;i<list.size();i++) {
                        if(!list_all.contains(list.get(i)))
                            list_all.add(list.get(i));
                    }
                    refreshDatas(list_all);
                }

                break;
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try{

                        if(list_all.size()==0){
                            list_all.add(R.drawable.add);
                        }
                        list_all.add(file.getAbsolutePath());
                        refreshDatas(list_all);
                    }catch (Exception e){

                    }
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initViews(){
        gridView=(GridView)findViewById(R.id.publish_gridview);
        editText=(EditText)findViewById(R.id.publish_edittext);
        view2= LayoutInflater.from(PublishActivity.this).inflate(R.layout.popwindow_photo,null);
        popupWindow=new PopupWindow(view2, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        networkLoader=NetworkLoader.getInstance();
        list_all=new ArrayList();
        list_all.add(R.drawable.add);
        refreshDatas(list_all);
    }

    public void refreshDatas(List list){
        adapter=new Publish_gridview_adapter(this,list);
        gridView.setAdapter(adapter);
    }

    public void initActionBar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.publish_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.publish_item:
                //准备将说说发表出去
                ArrayList<String> list=new ArrayList();
                if(list_all.get(0) instanceof Integer) {
                    if(list_all.size()>1) {
                        for (int i =1; i < list_all.size(); i++) {
                            list.add((String)list_all.get(i));
                        }
                    }
                }

                // 进行说说正确性检查
                if((list==null||list.size()==0)&&(editText.getText().toString()==null||editText.getText().toString().equals(""))){
                    Toast.makeText(this,"请输入说说内容",Toast.LENGTH_SHORT).show();
                }else {
                    NetworkLoader.getInstance().publish_Shuoshuo(editText.getText().toString(), list, this);
                }
                list_all.clear();
                break;

            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void takePhoto(){
        file=new File(getExternalCacheDir(),"mid_image"+System.currentTimeMillis()+".jpg");

        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        }catch (Exception e){

        }
        if(Build.VERSION.SDK_INT>=24){
            uri= FileProvider.getUriForFile(MyApplication.getContext(),"com.example.fileprovider",file);

        }else{
            uri=Uri.fromFile(file);
        }
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    //令屏幕变暗
    public void lightOff(){
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.alpha=0.3f;
        getWindow().setAttributes(layoutParams);
    }

    //令屏幕变亮
    public void lightOn(){
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.alpha=1.0f;
        getWindow().setAttributes(layoutParams);
    }
}
