package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.smartschool.smartschooli.R;

import java.util.List;

import bean.UpdateMessage_Bean;
import de.hdodenhof.circleimageview.CircleImageView;
import utils.ImageLoader;
import utils.NetworkLoader;

//聊天记录的适配器
public class FragChat_list_Adapter extends BaseAdapter {

    List<UpdateMessage_Bean> list;

    Context context;

    public FragChat_list_Adapter(List<UpdateMessage_Bean>list,Context context){
        this.context= context;
        this.list=list;
    }

    public void setList(List<UpdateMessage_Bean>list){
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.fragment_chatlist_item,viewGroup,false);
            viewHolder.circleImageView=(CircleImageView)view.findViewById(R.id.chat_list_item_image);
            viewHolder.textView_content=(TextView)view.findViewById(R.id.chat_list_item_content);
            viewHolder.textView_name=(TextView)view.findViewById(R.id.chat_list_item_name);
            viewHolder.textView_time=(TextView)view.findViewById(R.id.chat_list_item_time);
            viewHolder.reddot=(View)view.findViewById(R.id.chat_list_item_dot);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.reddot.setVisibility(View.GONE);
        UpdateMessage_Bean bean=list.get(i);
        ImageLoader.getInstance().loadImage(bean.getImageUrl(),viewHolder.circleImageView);
        viewHolder.circleImageView.setTag(bean.getImageUrl());


        //设置内容
        if(bean.getUpdate_type().equals("IMAGE")){
            viewHolder.textView_content.setText("[图片]");

        }else if(bean.getUpdate_type().equals("SOUND")){
            viewHolder.textView_content.setText("[语音]");
        }else if(bean.getUpdate_type().equals("MESSAGE")){
            viewHolder.textView_content.setText(bean.getUpdate_content());
        }
        if(bean.getUpdate_sender_name().equals(NetworkLoader.getInstance().getPersonMessage().get(2))){
            viewHolder.textView_name.setText(bean.getUpdate_receiver_name());
            viewHolder.textView_name.setTag(bean.getUpdate_receiverId());
        }else{
            viewHolder.textView_name.setText(bean.getUpdate_sender_name());
            viewHolder.textView_name.setTag(bean.getUpdate_senderId());
        }

        if(!bean.getHasread()&&!bean.getUpdate_senderId().equals(NetworkLoader.getInstance().getPersonMessage().get(0))){
            viewHolder.reddot.setVisibility(View.VISIBLE);
        }
        viewHolder.textView_time.setText(bean.getUpdatedAt());

        return view;
    }


    class ViewHolder{
        CircleImageView circleImageView;

        TextView textView_name;

        TextView textView_content;

        TextView textView_time;

        View reddot;
    }
}
