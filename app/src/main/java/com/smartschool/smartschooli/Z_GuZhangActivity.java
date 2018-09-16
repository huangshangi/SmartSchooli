package com.smartschool.smartschooli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import bean.Repair_Bean;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Z_GuZhangActivity extends AppCompatActivity {

    private int a, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guzhang_fenxi);

        final BmobQuery<Repair_Bean> bmobQuery = new BmobQuery<Repair_Bean>();
        bmobQuery.findObjects(new FindListener<Repair_Bean>() {
            @Override
            public void done(List<Repair_Bean> list, BmobException e) {
                if (e == null) {
                    a = 0;
                    b = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if(list.get(i).getRepair_type().equals("故障类型1")){
                            a = a+1;
                        }
                        if(list.get(i).getRepair_type().equals("故障类型2")){
                            b = b+1;
                        }
                    }
                    TextView type_1 = (TextView) findViewById(R.id.type_1);
                    type_1.setText((double)a / (a + b)*100 + "%");

                    TextView type_2 = (TextView) findViewById(R.id.type_2);
                    type_2.setText((double)b / (a + b) *100+ "%");
                } else {
                    System.out.println(e.getErrorCode());
                }
            }
        });

    }
}
