package com.example.horsey;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class Chart{
    private PieChart pie;
    private int totalscore;

    public Chart(PieChart pieChart,int totalscore){
        this.pie=pieChart;
        this.totalscore=totalscore;
    }
    public void run(){
        //*********此行代码需插入*************
        List<PieEntry> list;                                                //*********此行代码需插入*************
        list = new ArrayList<>();
        list.add(new PieEntry(totalscore, "分数"));                            //将20替换为最终所得分数
        list.add(new PieEntry((28-totalscore), "分数"));                             //将8替换为（28—最终所得分数）
        PieDataSet pieDataSet = new PieDataSet(list, "");
        PieData pieData = new PieData(pieDataSet);
        pie.setData(pieData);
        pie.setBackgroundColor(Color.TRANSPARENT);

        pieDataSet.setColors(Color.parseColor("#65B1BF"), Color.GRAY);
        pie.setHoleRadius(60);
        pie.setTransparentCircleRadius(60);
        pie.setHoleColor(Color.TRANSPARENT);
        pie.setCenterText("20"+"/28" + "分");                        //将20替换为最终所得分数（注：转换为字符串类型）
        pie.setCenterTextColor(Color.BLACK);
        pie.setCenterTextSize(28);
        pie.setDrawEntryLabels(false);
        pie.setBackgroundColor(Color.parseColor("#fafafa"));

        pieDataSet.setDrawValues(false);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.6f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart2Length(0.6f);
        pieDataSet.setValueLineColor(Color.TRANSPARENT);

        pie.getDescription().setEnabled(false);

        Legend legend = pie.getLegend();
        legend.setEnabled(false);

        pie.notifyDataSetChanged();
        pie.invalidate();
    }
}
