package com.smartschool.smartschooli;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.smartschool.smartschooli.R;

import java.util.ArrayList;
import java.util.List;

import bean.Repair_Bean;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Z_GuZhangActivity extends AppCompatActivity {

    private PieChart mPieChart;
    private int a, b, c, d;
    private BarChart barChart;
    private XAxis xAxis;
    List<String> values;

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
                    c = 0;
                    d = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getRepair_machine().equals("空调")) {
                            a = a + 1;
                        }
                        if (list.get(i).getRepair_machine().equals("投影仪")) {
                            b = b + 1;
                        }
                        if (list.get(i).getRepair_machine().equals("桌椅")) {
                            c = c + 1;
                        }
                        if (list.get(i).getRepair_machine().equals("电脑")) {
                            d = d + 1;
                        }
                        Log.d("GuzhangFenxi",a+" "+b+" "+c+" "+d);
                        PieChart();
                        BarChart();
                    }
                } else {
                    System.out.println(e.getErrorCode());
                }

            }
        });

    }

    private void setPieChartData(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "故障分布");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }

    private void PieChart() {
        mPieChart = (PieChart) findViewById(R.id.pie_chart);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 4);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文件
        mPieChart.setCenterText("故障分析");

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // 触摸旋转
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        //设置数据
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry(a*100/(a+b+c+d), "空调"));
        entries.add(new PieEntry(b*100/(a+b+c+d), "投影仪"));
        entries.add(new PieEntry(c*100/(a+b+c+d), "桌椅"));
        entries.add(new PieEntry(d*100 /(a+b+c+d), "电脑"));

        //设置数据
        setPieChartData(entries);

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // 输入标签样式
        mPieChart.setEntryLabelColor(Color.WHITE);
        mPieChart.setEntryLabelTextSize(12f);
    }

    private void BarChart() {
        values = new ArrayList<>();
        values.add("投影仪");
        values.add("空调");
        values.add("桌椅");
        values.add("电脑");

        barChart = (BarChart) findViewById(R.id.bar_chart);
        //1、基本设置
        xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        // 设置是否可以触摸
        barChart.setTouchEnabled(false);
        // 是否可以拖拽
        barChart.setDragEnabled(true);
        // 是否可以缩放
        barChart.setScaleEnabled(true);

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        Legend legend = barChart.getLegend();
        //是否显示
        legend.setEnabled(true);
        //图例样式：有圆点，正方形，短线 几种样式
        legend.setForm(Legend.LegendForm.CIRCLE);
        // 图例显示的位置：如下2行代码设置图例显示在左下角
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        // 图例的排列方式：水平排列和竖直排列2种
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        // 图例距离x轴的距离
        legend.setXEntrySpace(10f);
        //图例距离y轴的距离
        legend.setYEntrySpace(10f);
        //图例的大小
        legend.setFormSize(7f);
        // 图例描述文字大小
        legend.setTextSize(10);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (index < 0 || index >= values.size()) {
                    return "";
                }
                return values.get(index);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };

        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);
        //数据位于底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);


        //y轴数据
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        //new BarEntry(20, 0)前面代表数据，后面代码柱状图的位置；
        yValues.add(new BarEntry(0, a));
        yValues.add(new BarEntry(1, b));
        yValues.add(new BarEntry(2, c));
        yValues.add(new BarEntry(3, d));

        //设置显示的数字为整形
        BarDataSet barDataSet = new BarDataSet(yValues, "故障次数");
        //设置柱状图的颜色
        barDataSet.setColors(new int[]{Color.rgb(104, 202, 37), Color.rgb(192, 32, 32),
                Color.rgb(34, 129, 197), Color.rgb(175, 175, 175)});
        //显示，柱状图的宽度和动画效果
        BarData barData = new BarData(barDataSet);
        barChart.animateY(1000);
        barChart.setData(barData);
    }

}
