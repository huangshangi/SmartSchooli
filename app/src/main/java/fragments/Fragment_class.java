package fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.VolumeAutomation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.smartschool.smartschooli.FcTeacherActivity;
import com.smartschool.smartschooli.MainActivity;
import com.smartschool.smartschooli.R;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.Fragment_class_RecyclerView_adapter;
import adapter.Fragment_class_listView_adapter;
import adapter.ScrollAdapter;
import bean.Class_Bean;
import listener.Fragment_class_Change_Listener;
import listener.Fragment_class_Listener;
import listener.Wheel_listener;
import utils.MyApplication;
import utils.NetworkLoader;
import utils.Util;
import view.WheelView;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

//课堂界面
//scrollView与listView滑动冲突暂未处理
public class Fragment_class extends Fragment {

    final static int CAPTURE_QR=0x10;

    String type;

    Util util;

    View popupView;//弹出框控件

    View view;

    int midWeek;

    PopupWindow popupWindow_week;

    PopupWindow popupWindow_student;

    PopupWindow popupWindow_teacher;

    WheelView wheelView;

    int allWeek=25;//学期总周数

    int currentWeek=Util.getInstance().getCurrentWeek();//当前课程周数

    int  week=Util.getInstance().getCurrentWeek();//当前所显示的课程周数

    ArrayList<Class_Bean> list;

    List<String>list_popup;

    List<TextView>list_date;//储存显示日期的textView

    LinearLayout linearlayout;//隐藏布局

    PopupWindow popupWindow;



    int linearlayout_MaxHeight;//隐藏布局的显示完毕后高度 px

    int linearlayout_CurrentHeight;//隐藏布局的当前高

    boolean flag_anim;//判断是否正在进行动画

    Button button_changeWeek;//修改当前周数的按钮

    TextView alert_text;

    Button alert_button_cancel;

    Button alert_button_sure;

    RecyclerView recyclerView;

    Button button_current;//显示当前周数

    Fragment_class_RecyclerView_adapter adapter;

    TextView textView_Student_scan;

    TextView textView_Teacher_QR;

    ImageView imageView_add;

    ImageView imageView_menu;

    ListView listView;

    Handler handler;

    boolean flag_adapter=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_class_layout,container,false);

        initViews();

        initListeners();
        initEvents();
        refreshData(week);

        return view;
    }

    private void initEvents(){
        listView.setAdapter(new Fragment_class_listView_adapter(getActivity()));
        listView.setEnabled(false);

        button_current.setText("第"+week+"周");

        wheelView.setPAGE_COUNT(5);

        for(int i=0;i<allWeek;i++){
            list_popup.add("第"+(i+1)+"周");
        }
        wheelView.setList(list_popup);

        wheelView.setWheel_listener(new Wheel_listener() {
            @Override
            public void onSelected(int position) {
                midWeek=position-1;
                Log.d("!!!!!!!!!!",position+"!!!!!!");
                alert_text.setText(list_popup.get(position));

            }
        });


        list=NetworkLoader.getInstance().getList();

        list_date.add((TextView)view.findViewById(R.id.fragment_class_date1));
        list_date.add((TextView)view.findViewById(R.id.fragment_class_date2));
        list_date.add((TextView)view.findViewById(R.id.fragment_class_date3));
        list_date.add((TextView)view.findViewById(R.id.fragment_class_date4));
        list_date.add((TextView)view.findViewById(R.id.fragment_class_date5));
        list_date.add((TextView)view.findViewById(R.id.fragment_class_date6));
        list_date.add((TextView)view.findViewById(R.id.fragment_class_date7));

        NetworkLoader.getInstance().setFragment_class_listener(new Fragment_class_Listener() {
            @Override
            public void getClassDown(ArrayList<Class_Bean> list) {

                Fragment_class.this.list=list;
                //   获取数据并更新隐藏布局

                // 获取数据暂未实现，应当得到一个list<Class_Bean>
                if(flag_adapter) {
                    adapter = new Fragment_class_RecyclerView_adapter(list, week, allWeek);
                    adapter.setListener(new Fragment_class_Change_Listener() {
                        @Override
                        public void changeClass(int week) {
                            Fragment_class.this.week=week;
                            button_current.setText("第"+week+"周");
                            recyclerView.smoothScrollToPosition(week-1);

                            refreshData(week);
                        }
                    });
                    flag_adapter=false;
                    handler.sendEmptyMessage(0);
                }else{
//                        adapter.setMessage(list, week, allWeek);
                }





            }




        });


    }


    private void updateDate(int week){

        for(int i=1;i<8;i++){
            if(i==7){
                list_date.get(i-1).setText(Util.getInstance().getCurrentDay(2018,week+1,1));
            }else {
                list_date.get(i - 1).setText(Util.getInstance().getCurrentDay(2018, week, i+1));
            }
        }
    }

    //监听器
    private void initListeners(){

        imageView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("学生")) {
                    if (!popupWindow_student.isShowing()) {
                        showStudentPopupWindow();
                    }
                } else if(type.equals("教师")){

                    if (!popupWindow_teacher.isShowing()) {
                        showTeacherPopupWindow();
                    }
                }
            }
        });

        textView_Teacher_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FcTeacherActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("list",list);
                intent.putExtras(bundle);
                startActivity(intent);
                if(popupWindow_teacher.isShowing()){
                    popupWindow_teacher.dismiss();
                }
            }
        });

        textView_Student_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), CaptureActivity.class),CAPTURE_QR);
                Log.d("student","scan方法执行");
                if(popupWindow_student.isShowing()){
                    popupWindow_student.dismiss();
                }
            }
        });


        //弹出抽屉栏
        imageView_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getDrawerLayout().openDrawer(Gravity.START);
            }
        });
        //点击显示或收起隐藏布局
        button_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 检查当前隐藏布局显示状态
                if(linearlayout_CurrentHeight!=0){
                    //隐藏布局当前处于显示状态
                    Drawable drawable = getActivity().getResources().getDrawable(R.drawable.fragment_class_pull_up);
                    // 这一步必须要做,否则不会显示.

                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    button_current.setCompoundDrawables(null, null, drawable, null);
                    linearlayoutAnim(linearlayout_MaxHeight,0);

                    //将当前周数改为当前周并显示当前周课表


                    //显示某一周课表


                }else{
                    //当前布局处于隐藏状态
                    Drawable drawable = getActivity().getResources().getDrawable(R.drawable.fragment_class_pull_down);
                    // 这一步必须要做,否则不会显示.

                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    button_current.setCompoundDrawables(null, null, drawable, null);

                    linearlayoutAnim(0,linearlayout_MaxHeight);
                }

            }
        });


        button_changeWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wheelView.setSelection(week-1);
                alert_text.setText("第"+week+"周");

                synchronized (this) {
                    showAlertDialog();

                    showPopupWindow();
                }
                Util.getInstance().lightOff(getActivity());
            }
        });

        alert_button_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week=midWeek;
                button_current.setText("第"+week+"周");
                popupWindow.dismiss();
                popupWindow_week.dismiss();
                refreshData(week);
                synchronized (this) {
                    recyclerView.scrollToPosition(week - 1);

                    linearlayoutAnim(linearlayout_MaxHeight, 0);
                }
                Util.getInstance().lightOn(getActivity());
            }
        });

        alert_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
                popupWindow_week.dismiss();
                Util.getInstance().lightOn(getActivity());
            }
        });
    }

    private void showTeacherPopupWindow(){
        popupWindow_teacher.setTouchable(true);
        popupWindow_teacher.setOutsideTouchable(true);
        popupWindow_teacher.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popupWindow_teacher.showAsDropDown(imageView_add);
    }

    private void showStudentPopupWindow(){
        popupWindow_student.setTouchable(true);
        popupWindow_student.setOutsideTouchable(true);
        popupWindow_student.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popupWindow_student.showAsDropDown(imageView_add);
    }


    private void showAlertDialog(){
        popupWindow_week.setTouchable(true);
        popupWindow_week.setOutsideTouchable(false);
        popupWindow_week.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popupWindow_week.showAtLocation(view.findViewById(R.id.fragment_class_layout), Gravity.CENTER,0,0);
    }

    private void showPopupWindow(){

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popupWindow.showAtLocation(view.findViewById(R.id.fragment_class_layout), Gravity.BOTTOM,0,0);

    }

    //隐藏布局的动画  start end 分别代表隐藏布局动画结束前后高度
    private void linearlayoutAnim(int start, final int end){
        final ViewGroup.LayoutParams params=linearlayout.getLayoutParams();

        ValueAnimator valueAnimator=ValueAnimator.ofInt(start,end);
        valueAnimator.setDuration(300);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                linearlayout_CurrentHeight=(int)animation.getAnimatedValue();

                if(flag_anim) {
                    params.height = linearlayout_CurrentHeight;
                    linearlayout.setLayoutParams(params);
                }
                if(linearlayout_CurrentHeight==end){
                    flag_anim=false;
                    adapter.PopupWindowSelected(week);
                }else{
                    flag_anim=true;
                }
            }
        });

    }


    private void  refreshData(int week){
        //模拟从数据库获取数据
        list= Util.getInstance().getRealList(NetworkLoader.getInstance().getList(),week);
        updateDate(week);
        new ScrollAdapter(list,view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("student","onActivity方法之心血管");
        switch(requestCode){
            case CAPTURE_QR:
                if(resultCode==RESULT_OK){
                    Log.d("student","((("+(String)data.getExtras().get("result"));
                    String message=(String)data.getExtras().get("result");//二维码中包含信息

                    //判断是否为特定二维码
                    if(!message.contains("!")){
                        Toast.makeText(getActivity(),"签到失败,请检查二维码",Toast.LENGTH_SHORT).show();
                    }else{
                        //扫描成功,将签到信息上传
                        String[]array=message.split("!");
                        String cNQ=array[0];
                        String courseName=array[1];
                        String week=array[2];
                        String day=array[3];
                        String jieshu=array[3];

                        List<String>list=NetworkLoader.getInstance().getPersonMessage();
                        String name=list.get(5);
                        String xuehao=list.get(0);

                        NetworkLoader.getInstance().UpQiandao(cNQ,courseName,week,day,jieshu,name,xuehao);
                    }


                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViews(){

        LinearLayout linearLayout2=(LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.fragment_class_alertdialog,null);


        popupWindow_week=new PopupWindow(linearLayout2);

        popupWindow_week.setWidth(getDisplayMetris()-Util.getInstance().dp2px(100));
        popupWindow_week.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout=(LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_class_popup,null);
        wheelView=(WheelView)linearLayout.findViewById(R.id.fragment_class_WheelView) ;

        popupWindow=new PopupWindow(linearLayout,ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayoutStud=(LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.fragment_class_add_pop_student,null);
        popupWindow_student=new PopupWindow(linearLayoutStud,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        textView_Student_scan=(TextView)linearLayoutStud.findViewById(R.id.fragment_class_popStud_scan);

        LinearLayout linearLayout3=(LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_class_add_pop_teacher,null);
        popupWindow_teacher=new PopupWindow(linearLayout3,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textView_Teacher_QR=(TextView)linearLayout3.findViewById(R.id.fragment_class_popTeacher);


        alert_text=(TextView)linearLayout2.findViewById(R.id.fragment_class_alert_week);

        alert_button_cancel=(Button)linearLayout2.findViewById(R.id.fragment_class_alert_buttonCancel);

        alert_button_sure=(Button)linearLayout2.findViewById(R.id.fragment_class_alert_buttonSure);

        list=new ArrayList<>();

        list_popup=new ArrayList<>();

        list_date=new ArrayList<>();

        listView=(ListView)view.findViewById(R.id.fragment_class_listView);

        button_changeWeek=(Button)view.findViewById(R.id.fragment_class_inVisbile_button);

        recyclerView=(RecyclerView)view.findViewById(R.id.fragment_class_inVisbile_recyclerView);

        linearlayout=(LinearLayout)view.findViewById(R.id.fragment_class_inVisbile_layout);

        imageView_add=(ImageView)view.findViewById(R.id.fragment_class_imageView_add);

        imageView_menu=(ImageView)view.findViewById(R.id.fragment_class_imageView_menu);

        currentWeek=Util.getInstance().getCurrentWeek();

        week=currentWeek;

        util=Util.getInstance();

        linearlayout_MaxHeight=util.dp2px(120);

        button_current=(Button)view.findViewById(R.id.fragment_class_currentweek);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);

            }
        };
        type=NetworkLoader.getInstance().getPersonMessage().get(3);
    }

    //获取屏幕宽度
    private int getDisplayMetris(){
        return getResources().getDisplayMetrics().widthPixels;
    }

    //使屏幕变亮
    private void lightOn(){
        WindowManager.LayoutParams params=getActivity().getWindow().getAttributes();
        params.alpha=1.0f;
        getActivity().getWindow().setAttributes(params);
    }


    //使屏幕变暗
    private void lightOff(){
        WindowManager.LayoutParams params=getActivity().getWindow().getAttributes();
        params.alpha=0.3f;
        getActivity().getWindow().setAttributes(params);

    }



}
