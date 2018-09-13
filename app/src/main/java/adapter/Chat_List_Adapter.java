package adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.smartschool.smartschooli.R;

import java.util.HashMap;
import java.util.List;

import bean.Message_Bean;
import de.hdodenhof.circleimageview.CircleImageView;
import utils.ImageLoader;
import utils.NetworkLoader;

import static android.view.View.GONE;


//聊天面板适配器
public class Chat_List_Adapter extends BaseAdapter implements View.OnClickListener{

    Context context;

    AnimationDrawable animationDrawable;

    HashMap<AnimationDrawable,View> map;

    List<Message_Bean>list;
    public Chat_List_Adapter(Context context, List<Message_Bean> list){
        this.context=context;
        this.list=list;
        map=new HashMap<>();
    }

    public void setList(List<Message_Bean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        Log.d("&&&&&&","共有"+list.size()+"条数据");
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
        final ViewHolder viewHolder;
        //  if(view==null) {
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(context).inflate(R.layout.chat_layout_item, viewGroup, false);

        //初始化控件
        viewHolder.imageView_left = (CircleImageView) view.findViewById(R.id.chat_item_Leftimage);
        viewHolder.imageView_right = (CircleImageView) view.findViewById(R.id.chat_item_Rightimage);
        viewHolder.imageView_content_right=(ImageView)view.findViewById(R.id.chat_right_image);
        viewHolder.imageView_content_left=(ImageView)view.findViewById(R.id.chat_left_image);
        viewHolder.textView_left = (TextView) view.findViewById(R.id.chat_item_Lefttext);
        viewHolder.textView_right = (TextView) view.findViewById(R.id.chat_item_Righttext);
        viewHolder.View_left_cont = view.findViewById(R.id.chat_item_Leftcontimage);
        viewHolder.View_right_cont = view.findViewById(R.id.chat_item_Rightcontimage);
        view.setTag(viewHolder);
        // }else{
        //      viewHolder=(ViewHolder)view.getTag();
        //  }
        view.findViewById(R.id.chat_left).setVisibility(View.VISIBLE);
        view.findViewById(R.id.chat_right).setVisibility(View.VISIBLE);
        final Message_Bean bean=list.get(i);

        //该客户端是发送端

        if(bean.getSender_id().equals(NetworkLoader.getInstance().getPersonMessage().get(0))){
            view.findViewById(R.id.chat_left).setVisibility(GONE);
            view.findViewById(R.id.chat_right_frame).setVisibility(View.VISIBLE);
            viewHolder.imageView_right.setVisibility(View.VISIBLE);
            if(bean.getType().equals("MESSAGE")) {

                viewHolder.imageView_content_right.setVisibility(GONE);
                viewHolder.View_right_cont.setVisibility(GONE);
                viewHolder.textView_right.setVisibility(View.VISIBLE);
                viewHolder.textView_right.setText(bean.getContent());

            }else if(bean.getType().equals("SOUND")){

                viewHolder.View_right_cont.setVisibility(View.VISIBLE);
                viewHolder.imageView_content_right.setVisibility(GONE);

                viewHolder.View_right_cont.setBackgroundResource(R.drawable.adj_right);
                viewHolder.textView_right.setVisibility(View.GONE);

                viewHolder.View_right_cont.setTag(bean.getContent());
                viewHolder.View_right_cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(animationDrawable!=null){
                            animationDrawable.stop();
                            animationDrawable.selectDrawable(3);
                            animationDrawable=null;
                        }

                        viewHolder.View_right_cont.setBackgroundResource(R.drawable.sounder);
                        animationDrawable=(AnimationDrawable)viewHolder.View_right_cont.getBackground();
                        animationDrawable.start();
                        NetworkLoader.getInstance().playMedia(bean.getContent(),viewHolder.View_right_cont,1);

                    }
                });
            }else if(bean.getType().equals("IMAGE")){
                view.findViewById(R.id.chat_right_frame).setVisibility(GONE);
                Log.d("content","方法被执行"+bean.getContent());
                viewHolder.textView_right.setVisibility(GONE);
                viewHolder.View_right_cont.setVisibility(GONE);
                viewHolder.imageView_content_right.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().loadImage(bean.getContent(),viewHolder.imageView_content_right);
            }
            ImageLoader.getInstance().loadImage(NetworkLoader.getInstance().getPersonMessage().get(1),viewHolder.imageView_right);


        }else{
            //客户端是接收端
            view.findViewById(R.id.chat_right).setVisibility(GONE);
            viewHolder.imageView_left.setVisibility(View.VISIBLE);
            if(bean.getType().equals("MESSAGE")) {
                viewHolder.imageView_content_left.setVisibility(GONE);
                viewHolder.View_left_cont.setVisibility(GONE);
                viewHolder.textView_left.setVisibility(View.VISIBLE);
                viewHolder.textView_left.setText(bean.getContent());
            }else if(bean.getType().equals("SOUND")){
                viewHolder.imageView_content_left.setVisibility(GONE);
                viewHolder.View_left_cont.setVisibility(View.VISIBLE);
                viewHolder.textView_left.setVisibility(View.GONE);
                viewHolder.View_left_cont.setTag(bean.getContent());
                viewHolder.View_left_cont.setBackgroundResource(R.drawable.adj_left);
                viewHolder.View_left_cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        viewHolder.View_left_cont.setBackgroundResource(R.drawable.sounder_left);
                        if(animationDrawable!=null){
                            animationDrawable.stop();
                            animationDrawable.selectDrawable(3);
                        }
                        animationDrawable=(AnimationDrawable)viewHolder.View_left_cont.getBackground();
                        animationDrawable.start();

                        NetworkLoader.getInstance().playMedia(bean.getContent(),viewHolder.View_left_cont,2);

                    }
                });
            }else if(bean.getType().equals("IMAGE")){
                view.findViewById(R.id.chat_left_frame).setVisibility(GONE);
                viewHolder.textView_left.setVisibility(GONE);
                viewHolder.View_left_cont.setVisibility(GONE);
                viewHolder.imageView_content_left.setVisibility(View.VISIBLE);
                Log.d("接收端收到的图像地址",bean.getContent());
                ImageLoader.getInstance().loadImage(bean.getContent(),viewHolder.imageView_content_left);

            }

            ImageLoader.getInstance().loadImage(bean.getImage(),viewHolder.imageView_left);
            viewHolder.textView_right.setVisibility(GONE);
            viewHolder.imageView_right.setVisibility(GONE);
            viewHolder.View_right_cont.setVisibility(GONE);
        }

        //为imageView_content添加点击事件

        return view;
    }


    @Override
    public void onClick(View view) {

    }

    class ViewHolder{
        TextView textView_left;
        TextView textView_right;
        View View_left_cont;
        View View_right_cont;
        CircleImageView imageView_left;
        CircleImageView imageView_right;
        ImageView imageView_content_left;
        ImageView imageView_content_right;
    }
}
