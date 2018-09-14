package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.smartschool.smartschooli.ChatActivity;
import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import bean.Shuoshuo_Bean;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.CommitListener;
import utils.ImageLoader;
import utils.MyApplication;
import utils.NetworkLoader;

import static java.lang.Thread.sleep;

//说说适配器
public class RecyclerView_Adapter extends RecyclerView.Adapter {
    Context context;
    NetworkLoader networkLoader;
    List<Shuoshuo_Bean> list;
    static  CommitListener commitListener;
    boolean flag_commit=true;



    List<String>list_commit;//记录评论内容的列表
    public void setCommitListener(CommitListener commitListener) {
        this.commitListener = commitListener;
    }

    public void setList(List<Shuoshuo_Bean> list) {
        this.list = list;
    }

    public RecyclerView_Adapter(Context context, List<Shuoshuo_Bean> list){
        this.context=context;
        this.list=list;
        networkLoader=NetworkLoader.getInstance();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView textView_name;
        TextView textView_time;
        TextView shuoshuo_content;
        GridView gridView;
        ListView listView;
        EditText editText;
        Button button;
        View view_line;
        public ViewHolder(View view){
            super(view);
            imageView=(CircleImageView)view.findViewById(R.id.person_image);
            textView_name=(TextView)view.findViewById(R.id.person_name);
            textView_time=(TextView)view.findViewById(R.id.time);
            shuoshuo_content=(TextView)view.findViewById(R.id.shuoshuo_content);
            gridView=(GridView)view.findViewById(R.id.shuoshuo_gridview);
            listView=(ListView)view.findViewById(R.id.shuoshuo_commit_content);
            button=(Button)view.findViewById(R.id.shuoshuo_commit_send);
            editText=(EditText) view.findViewById(R.id.shuoshuo_commit_edittext);
            view_line=view.findViewById(R.id.view);
        }
        public void addListener(){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(commitListener!=null){
                        commitListener.commit();
                    }
                }
            });
        }
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.fragment_hall_recycler_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Shuoshuo_Bean bean=list.get(position);
        final ViewHolder viewHolder=(ViewHolder)holder;

        //为imageview设置标记,便于双方通信，为imageVIew设置了id
        viewHolder.imageView.setTag(bean.getPublisher_id());

        ImageLoader.getInstance().loadImage(bean.getPublisher_url(),viewHolder.imageView);
        viewHolder.textView_name.setText(bean.getPublisher_name());
        viewHolder.textView_time.setText(bean.getCreatedAt()+"");
        if(viewHolder.shuoshuo_content==null||viewHolder.shuoshuo_content.equals("")){
            viewHolder.shuoshuo_content.setText("");
        }else {
            viewHolder.shuoshuo_content.setText(bean.getPublished_content());
        }
        if(position==0){
            viewHolder.view_line.setVisibility(View.GONE);
        }else{
            viewHolder.view_line.setVisibility(View.VISIBLE);
        }

        //分别将urls commit转化成list
        viewHolder.gridView.setAdapter(new Publish_gridview_adapter(context,StringToList(bean.getPublished_urls())));
        ListAdapter_oneItem listAdapter_oneItem=new ListAdapter_oneItem(context,StringToList(bean.getPublished_commit()));
        viewHolder.listView.setAdapter(listAdapter_oneItem);
        addListener(viewHolder,bean,listAdapter_oneItem);
    }

    //监听器
    public void addListener(final ViewHolder viewHolder, final  Shuoshuo_Bean bean, final ListAdapter_oneItem listAdapter_oneItem){



        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyApplication.getContext(),"您点击了"+bean.getPublisher_id()+"的说说",Toast.LENGTH_SHORT).show();

                NetworkLoader.getInstance().messageHasRead(bean.getPublisher_id(),NetworkLoader.getInstance().getPersonMessage().get(0));
                //开启聊天活动,传递地参数徐商讨
                if(!bean.getPublisher_id().equals(NetworkLoader.getInstance().getPersonMessage().get(0))) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("own_id", networkLoader.getPersonMessage().get(0));
                    intent.putExtra("target_id", bean.getPublisher_id());
                    intent.putExtra("target_name", bean.getPublisher_name());

                    context.startActivity(intent);
                }
            }
        });

        viewHolder.button.setEnabled(false);
        viewHolder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()!=0){

                    viewHolder.button.setEnabled(true);
                    viewHolder.button.setTextColor(MyApplication.getContext().getResources().getColor(R.color.white));
                    viewHolder.button.setBackgroundResource(R.drawable.shuoshuo_evluate_yes);
                }else{

                    viewHolder.button.setEnabled(false);
                    viewHolder.button.setTextColor(MyApplication.getContext().getResources().getColor(R.color.gray));
                    viewHolder.button.setBackgroundResource(R.drawable.shuoshuo_evluate);
                }
            }
        });
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftPan(viewHolder.editText);
                try {
                    sleep(200);
                }catch (Exception e){

                }
                if(!viewHolder.button.isEnabled()){
                    Toast.makeText(context,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    String commit=bean.getPublished_commit()+"￥"+NetworkLoader.getInstance().getPersonMessage().get(2)+"说:"+viewHolder.editText.getText().toString();
                    bean.setPublished_commit(commit);
                    list_commit=StringToList(bean.getPublished_commit());

                    listAdapter_oneItem.setList(list_commit);
                    listAdapter_oneItem.notifyDataSetChanged();

                    viewHolder.editText.setText("");
                    Log.d("当前评论内容",commit);

                    //将评论内容提交到服务器
                    networkLoader.addCommit(bean);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    //将String转化为list
    private List<String> StringToList(String s){
        if(s==null||s.equals("")){
            return new ArrayList<>();
        }
        List<String> list= Arrays.asList(s.split("￥"));
        return list;
    }

    //将list转化为String
    private String ListToString(List<String> list){
        String result="";
        for(int i=0;i<list.size();i++){
            result+=list.get(i);
            if(i!=list.size()-1){
                result+="￥";
            }
        }
        return result;
    }

    //收起软键盘
    private void hideSoftPan(View view){
        InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
