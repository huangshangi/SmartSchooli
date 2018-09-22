package com.smartschool.smartschooli;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;




import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.Chat_List_Adapter;
import bean.Message_Bean;
import listener.AudioPrepareListener;
import listener.Message_Down_Listener;
import listener.UpMessage_Listener;
import utils.AudioManager;
import utils.NetworkLoader;
import utils.AudioDialogManager;
import utils.Util;
import view.Chat_ListView;

/*
    当点击用户信息进入该活动时，首先得到对方id和自己id,
 */

//聊天面板类
public class ChatActivity extends AppCompatActivity implements View.OnTouchListener,View.OnLongClickListener{



    //记录录音时状态
    final int NORMAL=10;//正常状态

    final int PREPARE_RECOED=11;//准备录音

    final int ISRECORDING=12;//正在录音状态

    final int WANT_TO_CANCEL=13;//将要取消录音

    final int SHORT=14;//录音时间过短

    int RECODE_STATE=NORMAL;//记录当前录音状态

    long firstTime;//录音按钮被按下时间

    long endTime;//录音按钮抬起时间

    int time;

    File file;//储存拍照的文件

    Uri uri;//储存拍照文件的uri

    Runnable runnable;//一直运行，用来统计音量大小

    AudioDialogManager audioDialogManger;

    AudioManager audioManager;


    Toolbar toolbar;

    TextView toolbar_text;

    Chat_ListView listView;

    EditText editText;

    Button button_send;//发送按钮

    Button button_talk;//按住说话按钮

    ImageView imageView_takephoto;//拍照按钮

    ImageView imageView_picture;//选择照片按钮

    ImageView imageView_talk;//用来切换文字与语音的按钮

    FrameLayout frameLayout;//内部包含应该适时切换的控件

    EditText framelayout_edit;

    Button framelayout_button;

    Chat_List_Adapter adapter;

    NetworkLoader networkLoader;

    List<Message_Bean> list;//RecyclerView的数据源

    public static String target_id;
    public static String own_id;
    public static String target_name;

    boolean flag=true;//判断是否是第一次刷新

    boolean input_type=true;//记录当前输入类型,true代表文本输入，false代表语音输入

    ActionBar actionBar;

    Handler handler;//用来更新UI组件

    final int MESSAGE=1;

    boolean flag_prepare=false;

    boolean flag_long=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        initViews();
        initDatas();
        initEvents();
        initAdapter();
        addListeners();
    }



    //初始化控件
    public void initViews(){
        frameLayout=(FrameLayout)findViewById(R.id.chat_framelayout);
        framelayout_button=(Button)findViewById(R.id.chat_button_talk);
        framelayout_edit=(EditText)findViewById(R.id.chat_edittext);
        toolbar=(Toolbar)findViewById(R.id.chat_toolbar);
        listView=(Chat_ListView)findViewById(R.id.chat_listView);
        editText=(EditText)findViewById(R.id.chat_edittext);
        button_send=(Button)findViewById(R.id.chat_button);
        button_talk=(Button)findViewById(R.id.chat_button_talk);
        toolbar_text=(TextView)findViewById(R.id.toolbar_text);
        imageView_talk=(ImageView)findViewById(R.id.chat_vocie);
        imageView_picture=(ImageView)findViewById(R.id.chat_picture);
        imageView_takephoto=(ImageView)findViewById(R.id.chat_takephoto);
        list=new ArrayList<>();
        networkLoader=NetworkLoader.getInstance();
        audioDialogManger=new AudioDialogManager(this);
        audioManager=AudioManager.getInstance();

        runnable=new Runnable(){
            @Override
            public void run() {
                while(flag_prepare){
                    try {
                        Thread.sleep(100);

                        time+=0.1;
                        handler.sendEmptyMessage(0x110);
                    }catch (Exception e){

                    }
                }
            }
        };

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0x110:
                        audioDialogManger.change_imageRight(audioManager.getLevel(7));
                        break;

                    case 0x111:
                        new Thread(runnable).start();
                        break;

                    case 0x222:
                        listView.setSelection(msg.arg1);
                    default:
                        updateUI((Message_Bean) msg.obj);

                }

            }
        };

    }

    //初始化事件
    public void initEvents(){
        Intent intent=getIntent();
        button_talk.setLongClickable(true);
        target_id=intent.getStringExtra("target_id");
        own_id= intent.getStringExtra("own_id");
        target_name=intent.getStringExtra("target_name");

        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        toolbar_text.setText(target_name);

        networkLoader.getMessage(own_id,target_id,0);


        audioManager.setListener(new AudioPrepareListener() {
            @Override
            public void wellPrepared() {
                flag_prepare=true;
                handler.sendEmptyMessage(0x111);//开启线程
            }
        });

        networkLoader.setUpMessage_listener(new UpMessage_Listener() {
            @Override
            public void MessageDown(Message_Bean bean) {
                Message message=new Message();
                message.obj=bean;
                handler.sendMessage(message);

            }
        });

        networkLoader.setMessage_down_listener(new Message_Down_Listener() {
            @Override
            public void getMessageDown(List<Message_Bean> list) {

                for(int i=0;i<list.size();i++){
                    ChatActivity.this.list.add(0,list.get(i));
                    Log.d("&&&&&&&&&&!!!!!",list.get(i)+"");
                }

                adapter=new Chat_List_Adapter(ChatActivity.this,ChatActivity.this.list);
                listView.setAdapter(adapter);
                Message message=Message.obtain();
                message.what=0x222;
                message.arg1=adapter.getCount()-1;
               handler.sendMessage(message);
                Log.d("测试文字地址",listView.getCount()+"");
                listView.hide_refresh();//隐藏下拉条

            }
        });



    }

    //添加点击事件
    public void addListeners(){
        //为语音、文字切换按钮添加点击事件
        imageView_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(framelayout_edit.getVisibility()==View.VISIBLE){
                    framelayout_edit.setVisibility(View.GONE);
                    framelayout_button.setVisibility(View.VISIBLE);
                    input_type=false;//语音输入

                    imageView_talk.setBackgroundResource(R.drawable.jianpan);
                    getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }else if(framelayout_button.getVisibility()==View.VISIBLE){
                    framelayout_edit.setVisibility(View.VISIBLE);
                    framelayout_button.setVisibility(View.GONE);
                    input_type=true;//文字输入
                    imageView_talk.setBackgroundResource(R.drawable.voice_button);

                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editText.getText().toString()!=null&&!editText.getText().toString().equals("")){
                    button_send.setBackgroundResource(R.drawable.chat_button_back_sele);
                    button_send.setEnabled(true);
                }else{
                    button_send.setBackgroundResource(R.drawable.chat_button_back);
                    button_send.setEnabled(false);
                }
            }
        });

        //为发送按钮添加点击事件
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = editText.getText().toString();
                if (content != null && !content.equals("")) {
                    //准备发表说说，进行准备
                    updateMessage(content,"MESSAGE");
                    editText.setText("");
                }else{



                    MediaPlayer mediaPlayer=new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(ChatActivity.this.getExternalCacheDir() + "/ceshisound.amr");
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                    }catch (Exception e){
                        Log.d("wenjian!!!","文件出错"+e.getMessage());
                    }
                }
            }
        });


        //为按住说话按钮设立事件
        button_talk.setOnTouchListener(this);

        //为按住说话按钮设立长按shijian
        button_talk.setOnLongClickListener(this);


        //为选择拍照按钮添加点击事件
        imageView_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        //为从相册中选择照片添加点击事件
        imageView_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this,PhotoSelector.class);
                intent.putExtra("isOne","ChatActivity");
                startActivityForResult(intent,1);
            }
        });


    }


    //拍照
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
            uri= FileProvider.getUriForFile(ChatActivity.this,"com.example.fileprovider",file);

        }else{
            uri= Uri.fromFile(file);
        }
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,0x119);
    }

    //消息的发送及listView的更新
    public void updateMessage(String content,String type){
        networkLoader.addMessage(own_id,target_id,NetworkLoader.getInstance().getPersonMessage().get(1),""+type,content);
    }

    //当有信息发送时，更新UI
    public void updateUI(Message_Bean bean){

        if(bean==null){

            return;
        }
        list.add(bean);

        if(flag) {
            adapter = new Chat_List_Adapter(ChatActivity.this, list);
            listView.setAdapter(adapter);
            flag = false;
        }else{
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }

        listView.setSelection(adapter.getCount()-1);
        listView.invalidate();
    }


    //初始化适配器
    public void initAdapter(){

        listView.setAdapter(adapter);

    }

    public void initDatas() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    //判断当前用户是否想结束录音
    private boolean wantToCancel(int x,int y){
        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        if(x>imageView_talk.getWidth()+button_talk.getWidth()||x<imageView_talk.getWidth()){
            return true;
        }
        //y方向涉及单位换算，暂未处理


        return false;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(input_type){
            return super.onTouchEvent(motionEvent);
        }
        //如果刚开始时权限录音被拒绝，进行申请
        LoginActivity.requestPremission(ChatActivity.this, Manifest.permission.RECORD_AUDIO);

        int x=(int)motionEvent.getX();
        int y=(int)motionEvent.getY();
        switch (motionEvent.getAction()) {


            case MotionEvent.ACTION_DOWN:
                firstTime=System.currentTimeMillis();
                RECODE_STATE = PREPARE_RECOED;
                button_talk.setText("松开结束");
                if (flag_prepare) {
                    audioDialogManger.showrecodingDialog();
                }
                break;


            case MotionEvent.ACTION_MOVE:

                if(wantToCancel(x,y)&&flag_prepare){
                    RECODE_STATE=WANT_TO_CANCEL;

                    button_talk.setText("松开手指,取消发送");

                    audioDialogManger.showwant_to_cancelDialog();
                }else if(!wantToCancel(x,y)&&flag_prepare){
                    RECODE_STATE = ISRECORDING;
                    audioDialogManger.showrecodingDialog();

                }

                break;
            case MotionEvent.ACTION_UP:
                endTime=System.currentTimeMillis();
                Log.d("statue!!",RECODE_STATE+"");
                if(RECODE_STATE==ISRECORDING){
                    audioDialogManger.dismissDialog();
                    audioManager.stop();
                    updateMessage("","SOUND");

                }else if(RECODE_STATE==WANT_TO_CANCEL){
                    audioDialogManger.dismissDialog();
                    audioManager.cancel();
                }else if(endTime-firstTime<1000&&flag_long){
                    audioDialogManger.showtooShortDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            audioDialogManger.dismissDialog();
                        }
                    },500);
                }
                RECODE_STATE=NORMAL;
                button_talk.setText("按住 说话");
                flag_prepare=false;
                flag_long=false;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("!!!ddd","zhixing");
        audioManager.start();
        audioDialogManger.initRecordingDialog();
        flag_long=true;
        //长点击事件被触发，开始录音

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0x119:
                //从相机选择照片
                if(resultCode==RESULT_OK) {
                    updateMessage(file.getAbsolutePath(), "IMAGE");
                }
                break;

            case 1:
                //从相册选择照片
                if(resultCode==RESULT_OK){

                    String url=data.getStringArrayListExtra("list").get(0);
                    updateMessage(url, "IMAGE");
//                    PublishActivity.list_all.clear();



                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
