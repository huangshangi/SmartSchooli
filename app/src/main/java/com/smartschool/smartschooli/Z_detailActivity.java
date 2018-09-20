package com.smartschool.smartschooli;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import bean.Repair_Bean;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import utils.NetworkLoader;
import view.MyImageView;
import view.Submit_repair_GridView;


public class Z_detailActivity extends AppCompatActivity {

    private RatingBar ratingBar1;
    private RatingBar ratingBar2;
    private RatingBar ratingBar3;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Button repair;

    String array[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_detaillayout);

        //隐藏标题栏


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final Repair_Bean things = (Repair_Bean) getIntent().getSerializableExtra("thing");

        //设置各项内容
        TextView repair_name = (TextView) findViewById(R.id.submit_repair_name);
        repair_name.setText(things.getRepairer_name());

        TextView repair_machine = (TextView) findViewById(R.id.submit_repair_machine);
        repair_machine.setText(things.getRepair_machine());

        TextView repair_type = (TextView) findViewById(R.id.submit_repair_style);
        repair_type.setText(things.getRepair_type());

        TextView repair_address = (TextView) findViewById(R.id.submit_repair_address);
        repair_address.setText(things.getRepair_adress());

        TextView repair_content = (TextView) findViewById(R.id.submit_repair_content);
        repair_content.setText(things.getRepair_content());

        repair = (Button) findViewById(R.id.submit_repair_button);
        if (things.getHandle()) {
            repair.setText("已维修");
        } else {
            repair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    things.setHandle(true);
                    things.setRepair_person(NetworkLoader.getInstance().getPersonMessage().get(0));
                    things.update(things.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(Z_detailActivity.this, "维修成功", Toast.LENGTH_SHORT).show();
                                repair.setText("已维修");
                            } else {
                                Toast.makeText(Z_detailActivity.this, "无网络连接", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            });
        }


        if (things.getEvluate_status().equals("是")) {
            //评价组件
            ratingBar1 = (RatingBar) findViewById(R.id.ratingResult);
            textView1 = (TextView) findViewById(R.id.textView1);
            ratingBar2 = (RatingBar) findViewById(R.id.ratingSpeed);
            textView2 = (TextView) findViewById(R.id.textView2);
            ratingBar3 = (RatingBar) findViewById(R.id.ratingEvaluate);
            textView3 = (TextView) findViewById(R.id.textView3);
            array = new String[]{"非常差", "差", "一般", "好", "非常好"};

            int index = things.getEvluate_content().lastIndexOf("￥");
            String[] fenshus = things.getEvluate_content().substring(0, index).split("￥");

            int fenshu1 = Integer.valueOf(fenshus[0]);
            int fenshu2 = Integer.valueOf(fenshus[1]);
            int fenshu3 = Integer.valueOf(fenshus[2]);
            ratingBar1.setRating(fenshu1);
            textView1.setText(array[fenshu1 - 1]);
            ratingBar2.setRating(fenshu2);
            textView2.setText(array[fenshu2 - 1]);
            ratingBar3.setRating(fenshu3);
            textView3.setText(array[fenshu3 - 1]);
            TextView evaluate_content = (TextView) findViewById(R.id.evaluate_content);
            evaluate_content.setText(things.getEvluate_content().substring(index + 1));
        } else {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.evaluate);
            linearLayout.setVisibility(View.GONE);
        }

        String[] url = things.getRepair_urls().split("￥");

        Submit_repair_GridView submit_repair_gridView = (Submit_repair_GridView) findViewById(R.id.submit_repair_gridview);
//        LinearLayout llGroup = (LinearLayout) findViewById(R.id.image_group);
//        addGroupImage(url,submit_repair_gridView);

    }

    private void addGroupImage(String[] data, Submit_repair_GridView llGroup) {
//        llGroup.removeAllViews();
        for (int i = 0; i < data.length; i++) {
            MyImageView imageView = new MyImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageURL(data[i]);
            imageView.setBackgroundResource(android.R.color.white);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            lp.setMargins(0, 0, 0, 10);
            imageView.setLayoutParams(lp);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            llGroup.addView(imageView);
        }

    }
}
