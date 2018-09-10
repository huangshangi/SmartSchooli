package com.smartschool.smartschooli;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;




import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapter.PhotoSelector_adapter;
import bean.Group_ImageBean;
import listener.PopupListener;
import utils.NetworkLoader;
import view.ListViewPopupWindow;


/*
    图片选择界面,该类同时被发表说说界面和选择头像界面使用
    用isOne变量来区分当前该类被哪个活动调用
    if(isOne==true) 则说明被选择头像界面调用，此时应该只返回一张图片的url地址
    if(isOne==false)  则说明被发表说说界面调用，此时应该返回多张图片的url地址
    使用方法：
        Intent intent=new Intent(context,PhotoSelector.class)
        intent.putExtra("isOne",此处参上)
 */
public class PhotoSelector extends AppCompatActivity implements PopupListener {

    boolean isOne;
    List<String>select_list;//每次选择的照片
    List<Group_ImageBean> group_list;//储存组名，第一张图片，组中图片张数
    List<String> list;//其中一组的数据
    ProgressDialog progressBar;
    ListViewPopupWindow popupWindow;
    Button bottom_button;
    TextView bottom_text;
    Handler handler;
    GridView gridView;
    PhotoSelector_adapter adapter;
    final static int LOADING_DOWN=1;
    final static int ASK_PREMISSION=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photoselector_layout);

        initViews();
        initActionbar();
        initDatas();
        initListeners();
    }

    //为actionbar设置菜单
    public void initActionbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.photoselector_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("选择图片");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.button_send:

                //将选择的图片返回给上一个活动
                Intent intent=new Intent();
                intent.putStringArrayListExtra("list",PhotoSelector_adapter.list_selected);
                setResult(RESULT_OK,intent);

                PhotoSelector.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photosele_menu,menu);
        return true;
    }

    public void initListeners(){
        bottom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow=new ListViewPopupWindow(PhotoSelector.this,group_list);
                popupWindow.setPopupListener(new PopupListener() {
                    @Override
                    public void onSelect(int position) {
                        popupWindow.dismiss();
                        refreshDatas(position);
                    }
                });

                popupWindow.showAsDropDown(bottom_button,-300,0);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        lightOn();
                    }
                });
                lightOff();
            }
        });



    }
    public void initViews(){
        gridView=(GridView)findViewById(R.id.photoselector_gridview);
        progressBar= ProgressDialog.show(this,null,"loading...");
        group_list=new ArrayList<>();
        list = new ArrayList();
        select_list=new ArrayList<>();
        isOne=getIntent().getBooleanExtra("isOne",false);
        bottom_button=(Button)findViewById(R.id.photoselector_bottom_button);
        bottom_text=(TextView)findViewById(R.id.photoselector_bottom_text);
        //初始化handler对象
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case LOADING_DOWN:
                        progressBar.dismiss();
                        refreshDatas(0);
                        break;
                }
            }
        };
    }

    //查看是否具有权限
    public boolean checkPremission(String permission){
        if(ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},ASK_PREMISSION);
        }else{
            return true;
        }
        return false;
    }



    public void initDatas(){

        //进行权限的判断
        boolean flag=checkPremission( Manifest.permission.READ_EXTERNAL_STORAGE);
        if(flag==true) {

            //从手机内存中得到照片
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Set<String> hashSet = new HashSet();//只储存组名
                    Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media.MIME_TYPE
                            + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        if (hashSet.contains(parentFile.getAbsolutePath())) {
                            continue;
                        } else {
                            hashSet.add(parentFile.getAbsolutePath());
                            Group_ImageBean group_imageBean = new Group_ImageBean();
                            if(new File(path).isDirectory()){
                                continue;
                            }
                            group_imageBean.setPath(path);

                            if(parentFile.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File file, String filename) {
                                    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
                                        return true;
                                    return false;
                                }
                            })==null){
                                continue;
                            }
                            int count = parentFile.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File file, String filename) {
                                    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
                                        return true;
                                    return false;
                                }
                            }).length;
                            group_imageBean.setCount(count);
                            group_list.add(group_imageBean);
                        }
                    }
                    cursor.close();

                    handler.sendEmptyMessage(LOADING_DOWN);
                }

            }).start();
        }
    }


    //权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case ASK_PREMISSION:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(PhotoSelector.this,"授权成功",Toast.LENGTH_SHORT).show();
                    initDatas();
                }else{
                    Toast.makeText(PhotoSelector.this,"授权失败",Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void refreshDatas(int position){
        File []files=new File(group_list.get(position).getGroup_path()).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(s.endsWith(".jpg")||s.endsWith(".jepg")||s.endsWith(".png")){
                    return true;
                }
                return false;
            }
        });
        list.clear();
        for(int i=0;i<files.length;i++){
            list.add(files[i].getAbsolutePath());
        }
        if(list!=null) {

            adapter=new PhotoSelector_adapter(PhotoSelector.this,list,new File(group_list.get(position).getGroup_path()).getAbsolutePath(),isOne);

            gridView.setAdapter(adapter);

        }else{
            Log.d("wee","空");
        }
        bottom_button.setText(group_list.get(position).getName());
        bottom_text.setText(files.length+"张");
    }

    //从目录中得到文件
    public List getFiles(String path){
        List list=new ArrayList();
        if(new File(path).isDirectory()){
            for(File file:new File(path).listFiles()){
                if(file.isDirectory()){
                    getFiles(file.getAbsolutePath());
                }else{
                    list.add(file);
                }
            }
        }
        return list;
    }

    @Override
    public void onSelect(int position) {
        popupWindow.dismiss();
        refreshDatas(position);
    }

    //屏幕变暗
    public void lightOff(){
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.alpha=0.3f;
        getWindow().setAttributes(layoutParams);
    }

    //屏幕变亮
    public void lightOn(){
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.alpha=1.0f;
        getWindow().setAttributes(layoutParams);
    }
}
