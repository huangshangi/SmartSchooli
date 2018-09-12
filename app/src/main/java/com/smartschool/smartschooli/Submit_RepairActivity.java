package com.smartschool.smartschooli;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.Publish_gridview_adapter;
import listener.UpRepairListener;
import utils.MyApplication;
import utils.NetworkLoader;
import view.Submit_repair_GridView;

//维修界面
public class Submit_RepairActivity extends AppCompatActivity {

    public  static ArrayList list_all=new ArrayList<>();

    TextView textView_name;//报修人name，setTag(id)

    EditText editText_address;//报修地址,setTag(报修编号)

    EditText editText_machine;//故障设备


    EditText editText_content;//报修内容

    Submit_repair_GridView gridView;//报修的图片

    Button button_submit;//确认提交按钮

    Button button_select;

    File file;//暂时储存拍照图片

    PopupWindow popupWindow;

    View view2;//popupWindow的布局文件

    Publish_gridview_adapter adapter;

    NetworkLoader networkLoader;

    Uri uri;



    String type;//故障类型


    final int FROM_ALUBM=1;
    final int TAKE_PHOTO=0;
    final int ASK_PREMISSION=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.submit_repair_layout);
        initViews();
        initEvents();
        initListeners();
    }



    public void initViews(){
        textView_name=(TextView)findViewById(R.id.submit_repair_name);
        editText_address=(EditText)findViewById(R.id.submit_repair_address);
        editText_machine=(EditText)findViewById(R.id.submit_repair_machine);
        editText_content=(EditText)findViewById(R.id.submit_repair_content);
        gridView=(Submit_repair_GridView)findViewById(R.id.submit_repair_gridview);
        button_submit=(Button)findViewById(R.id.submit_repair_button);
        button_select=(Button)findViewById(R.id.submit_repair_style);
        networkLoader=NetworkLoader.getInstance();

        view2=LayoutInflater.from(this).inflate(R.layout.popwindow_photo,null);
        popupWindow=new PopupWindow(view2, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        if(list_all==null){
           list_all=new ArrayList<>();
        }

    }

    public void initEvents(){
       list_all.clear();
        list_all.add(R.drawable.add);
        refreshDatas(list_all);
        textView_name.setText(NetworkLoader.getInstance().getPersonMessage().get(2));
        networkLoader.setUpRepairListener(new UpRepairListener() {
            @Override
            public void upDown() {
                finish();
            }
        });
    }

    public void initListeners(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    //用户点击add按钮，弹出popwindow，让用户选择从相册中选取还是拍摄

                    if (popupWindow.isShowing()) {

                    } else {
                        lightOff();
                        showPopWindow();
                    }
                }


            }
        });

        //上传按钮
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (canUpload()) {
                   List<String> list=networkLoader.getPersonMessage();
                    networkLoader.prepareRepairMessage(list.get(0),list.get(2),editText_machine.getText().toString(),button_select.getText().toString(),editText_address.getText().toString()
                            ,RandomFigure(),editText_content.getText().toString(),list_all,Submit_RepairActivity.this);
                   // 将信息上传

                }else{
                    Toast.makeText(Submit_RepairActivity.this,"请填写完整信息",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //选择故障类型按钮
        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String []array=new String[]{"故障类型1", "故障类型2", "故障类型3", "故障类型4"};
                type=array[0];
                final AlertDialog.Builder builder=new AlertDialog.Builder(Submit_RepairActivity.this).setTitle("故障类型").setIcon(R.drawable.add)
                        .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                type=array[i];
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        button_select.setText(type);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });
    }

    //进入photoselector界面，并等待返回用户选择的图片
    public void fromAlubm(){

        Intent intent=new Intent(this,PhotoSelector.class);
        intent.putExtra("isOne","Submit_RepairActivity");
        startActivityForResult(intent,FROM_ALUBM);

    }




    //检查维修信息可上传性
    public boolean canUpload(){
        if(editText_machine.getText().toString()==null||editText_address.getText().toString()==null){
            return false;
        }else if(editText_address.getText().toString().equals("")||editText_machine.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    public boolean checkPremission(String permission){
        if(ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},ASK_PREMISSION);
        }else{
            return true;
        }
        return false;
    }

    //随机生成4位数
    public int RandomFigure(){
        return (int)(Math.random()*10000+1);
    }

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

        View conView= LayoutInflater.from(this).inflate(R.layout.submit_repair_layout,null);
        popupWindow.showAtLocation(conView, Gravity.BOTTOM,0,0);
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
            uri= Uri.fromFile(file);
        }
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,TAKE_PHOTO);

    }

    //返回用户选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case FROM_ALUBM:
                if(resultCode==RESULT_OK){
                    List<String> list=data.getStringArrayListExtra("list");
                    for(int i=0;i< list.size();i++)
                        if(!list_all.contains(list.get(i)))
                            list_all.add((String) list.get(i));

                    if(list_all.size()>0)
                        if(!(list_all.get(0) instanceof Integer))
                            list_all.add(0,R.drawable.add);
                    refreshDatas(list_all);


                        Log.d("Tagtag!!",""+list_all.size()+(list_all.get(0) instanceof Integer)+"@");
                }

                break;
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try{

                        if(!(list_all.get(0) instanceof Integer))
                            list_all.add(0,R.drawable.add);
                        list_all.add(file.getAbsolutePath());
                        refreshDatas(list_all);
                    }catch (Exception e){

                    }
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshDatas(List list){
        adapter=new Publish_gridview_adapter(this,list);
        gridView.setAdapter(adapter);
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
